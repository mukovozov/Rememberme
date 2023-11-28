@file:OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)

package com.remember.rememberme.feature.card.ui.cards

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.remember.rememberme.R
import com.remember.rememberme.feature.card.data.models.CardSet
import com.remember.rememberme.ui.components.Header
import com.remember.rememberme.ui.components.InputAlertDialog
import com.remember.rememberme.ui.components.RememberCard
import com.remember.rememberme.ui.theme.LightGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
inline fun <reified T> Flow<T>.collectAsSharedFlowWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline action: suspend (T) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        lifecycleOwner.repeatOnLifecycle(minActiveState) {
            collect(action)
        }
    }
}

@Composable
fun CardsScreenRoute(
    onBackButtonPressed: () -> Unit,
    viewModel: CardsViewModel = hiltViewModel(),
) {
    val cardsUiState by viewModel.cardsUiState.collectAsStateWithLifecycle()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    if (viewState.isInputDialogVisible) {
        AnswerInputDialog(
            onDialogDismissed = viewModel::onDialogDismissed,
            onConfirmButtonPressed = {
                viewModel.onInputConfirmed(viewState.activeCardIndex, it)
            }
        )
    }

    viewModel.events.collectAsSharedFlowWithLifecycle { event ->
        when (event) {
            is CardsScreenEvents.GoToNextCard -> {
                listState.animateScrollToItem(viewState.activeCardIndex)
            }
        }
    }

    val recognition = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { activityResult ->
            if (activityResult.resultCode != RESULT_OK) {
                return@rememberLauncherForActivityResult
            }

            activityResult.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let {
                viewModel.onSpeechRecognized(viewState.activeCardIndex, it)
            }
        })


    when (val state = cardsUiState) {
        is CardsUiState.Success -> {
            val set = state.set ?: return

            CardsScreen(
                set = set,
                viewState = viewState,
                listState = listState,
                onMicButtonClicked = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        // TODO: get from set
//                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "iw-IL")
                    }

                    recognition.launch(intent)
                },
                onKeyboardButtonClicked = viewModel::onKeyboardButtonClicked,
                onCheckButtonClicked = viewModel::onCheckButtonClicked,
                onSkipButtonClicked = viewModel::onSkipButtonClicked,
                onBackButtonPressed = {
                    onBackButtonPressed.invoke()
                }
            )
        }

        else -> {
            // do nothing for now
        }
    }
}

@Composable
private fun AnswerInputDialog(
    onDialogDismissed: () -> Unit,
    onConfirmButtonPressed: (String) -> Unit,
) {
    var input by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    InputAlertDialog(
        title = { Text(text = "Input value") },
        content = {
            OutlinedTextField(
                value = input,
                singleLine = true,
                onValueChange = {
                    input = it
                },
                modifier = Modifier.focusRequester(focusRequester)
            )
        },
        dismissButton = {
            TextButton(onClick = { onDialogDismissed.invoke() }) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmButtonPressed.invoke(input)
            }
            ) {
                Text(text = "OK")
            }
        },
        onDismiss = {
            onDialogDismissed.invoke()
        })

    LaunchedEffect(key1 = focusRequester, block = {
        awaitFrame()
        focusRequester.requestFocus()
    })
}

private fun LazyListState.isHalfPastItemRight(): Boolean {
    return firstVisibleItemScrollOffset > 500
}

private fun LazyListState.isHalfPastItemLeft(): Boolean {
    return firstVisibleItemScrollOffset <= 500
}

private fun CoroutineScope.scrollBasic(listState: LazyListState, left: Boolean = false) {
    launch {
        val pos = if (left) listState.firstVisibleItemIndex else listState.firstVisibleItemIndex + 1
        listState.animateScrollToItem(pos)
    }
}

@Composable
private fun CardsScreen(
    set: CardSet,
    viewState: CardsViewState,
    listState: LazyListState,
    onBackButtonPressed: () -> Unit,
    onMicButtonClicked: ((cardIndex: Int) -> Unit),
    onKeyboardButtonClicked: ((cardIndex: Int) -> Unit),
    onCheckButtonClicked: ((cardIndex: Int) -> Unit),
    onSkipButtonClicked: ((cardIndex: Int) -> Unit),
) {
    Box {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBackButtonPressed.invoke() },
                contentDescription = null,
                tint = Color.White,
            )

            Header(
                text = set.name,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LinearProgressIndicator(
                progress = (viewState.activeCardIndex + 1) / set.cards.size.toFloat(),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(24.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
            )


            val coroutineScope = rememberCoroutineScope()

            LazyRow(
                state = listState,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(266.dp),
                flingBehavior = rememberSnapFlingBehavior(listState),
                userScrollEnabled = false,
            ) {
                items(set.cards) { card ->
                    RememberCard(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(end = 16.dp),
                        height = 250.dp
                    ) {
                        Text(
                            text = card.text,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(top = 32.dp)
                                .align(Alignment.CenterHorizontally),
                        )

                        Text(
                            text = card.example,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(top = 32.dp)
                                .padding(horizontal = 32.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }

//                    if (!listState.isScrollInProgress) {
//                        if (listState.isHalfPastItemLeft()) {
//                            coroutineScope.scrollBasic(listState, left = true)
//                        } else {
//                            coroutineScope.scrollBasic(listState, left = false)
//                        }
//
//                        if (listState.isHalfPastItemRight()) {
//                            coroutineScope.scrollBasic(listState, left = false)
//                        } else {
//                            coroutineScope.scrollBasic(listState, left = true)
//                        }
//                    }
                }
            }

            if (viewState.currentAnswer.isNotEmpty() && !viewState.isRecognitionSuccessful) {
                ScoreCard(viewState)
            }
        }

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                iconResId = R.drawable.ic_cross,
                buttonColors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                onButtonClicked = { onSkipButtonClicked.invoke(viewState.activeCardIndex) }
            )

            ActionButton(
                iconResId = R.drawable.ic_mic,
                onButtonClicked = {
                    onMicButtonClicked.invoke(viewState.activeCardIndex)
                })

            ActionButton(
                iconResId = R.drawable.ic_keyboard,
                onButtonClicked = {
                    onKeyboardButtonClicked.invoke(viewState.activeCardIndex)
                }
            )

            ActionButton(
                iconResId = R.drawable.ic_check,
                buttonColors = ButtonDefaults.buttonColors(containerColor = LightGreen),
                onButtonClicked = { onCheckButtonClicked.invoke(viewState.activeCardIndex) }
            )
        }
    }
}

@Composable
private fun ScoreCard(viewState: CardsViewState) {
    RememberCard(
        modifier = Modifier.padding(top = 32.dp),
        height = 100.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier) {
                Text(
                    text = "SCORE",
                    modifier = Modifier,
                    color = Color.Black,
                    fontSize = 20.sp
                )
                Spacer(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .background(MaterialTheme.colorScheme.tertiary)
                        .height(1.dp)
                        .padding(top = 8.dp)
                        .width(56.dp)
                )
                Text(
                    text = "${viewState.correctnessPercents}",
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(
                Modifier
                    .padding(end = 32.dp)
            ) {
                Text(
                    text = "ANSWER",
                    modifier = Modifier,
                    color = Color.Black,
                    fontSize = 20.sp
                )
                Spacer(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .background(MaterialTheme.colorScheme.tertiary)
                        .height(1.dp)
                        .padding(top = 8.dp)
                        .width(80.dp)
                )
                Text(
                    text = viewState.currentAnswer,
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.Black,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun RowScope.ActionButton(
    @DrawableRes
    iconResId: Int,
    onButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        colors = buttonColors,
        onClick = {
            onButtonClicked.invoke()
        }, modifier = modifier
            .align(Alignment.CenterVertically)
            .padding(bottom = 16.dp)
            .height(64.dp),
        shape = CircleShape
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = null,
            modifier = Modifier,
            tint = Color.Black
        )
    }
}

//@Preview
//@Composable
//fun CardsScreenPreview() {
//    CardsScreen(
//        set = CardSet(1, "Daily Conversation", emptyList()),
//        CardsViewState(),
//        rememberLazyListState(),
//        {},
//        {},
//        {},
//        {}) {
//
//    }
//}

@Preview
@Composable
fun ScorePreview() {
    ScoreCard(
        viewState = CardsViewState(
            activeCardIndex = 1,
            isRecognitionSuccessful = false,
            score = 0,
        )
    )
}