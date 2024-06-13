@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.remember.rememberme.feature.card.ui.create_set

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.ui.theme.Black
import com.remember.rememberme.ui.theme.Pink80
import com.remember.rememberme.ui.theme.Purple40
import com.remember.rememberme.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

@Composable
fun CreateSetScreenRoute(
    onBackPressed: () -> Unit,
    viewModel: CreateSetViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel.events) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.consumeAsFlow()
                .collect { event ->
                    when (event) {
                        is CreateSetEvent.GoBack -> {
                            onBackPressed.invoke()
                        }
                        is CreateSetEvent.ShowMessage -> {
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    CreateSet(
        viewState = viewState,
        onBackPressed = onBackPressed,
        onGenerateButtonClicked = viewModel::onGenerateButtonClicked,
        onSubmitSetButtonClicked = viewModel::onSetSubmitButtonClicked,
        onTextChanged = viewModel::onTextChanged,
        onTranslationChanged = viewModel::onTranslationChanged,
        onExampleChanged = viewModel::onExampleChanged,
        onCardRemoved = viewModel::onCardRemoved
    )
}

@Composable
fun CreateSet(
    viewState: CreateSetViewState,
    onBackPressed: () -> Unit,
    onGenerateButtonClicked: (String) -> Unit,
    onSubmitSetButtonClicked: () -> Unit,
    onTextChanged: (Card, String) -> Unit,
    onTranslationChanged: (Card, String) -> Unit,
    onExampleChanged: (Card, String) -> Unit,
    onCardRemoved: (Card) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple40,
                    actionIconContentColor = Black,
                    navigationIconContentColor = Black
                ),
                title = { Text(text = "Create set", color = Black) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                },
                actions = {
                    IconButton(onClick = onSubmitSetButtonClicked) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = "")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
                .padding(it)
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Describe below in a few words what is the theme you want to create flashcards for.",
                color = White,
            )
            var input by remember { mutableStateOf("") }
            val keyboardController = LocalSoftwareKeyboardController.current
            TextField(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                value = input,
                onValueChange = {
                    input = it
                },
                label = {
                    Text(text = "Theme")
                },
                placeholder = {
                    Text(text = "modern slang, something from tiktok")
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    onGenerateButtonClicked.invoke(input)
                    keyboardController?.hide()
                })
            )

            Button(modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.CenterHorizontally),
                enabled = !viewState.isSetGenerating,
                onClick = {
                    onGenerateButtonClicked.invoke(input)
                    keyboardController?.hide()
                }) {
                if (viewState.isSetGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically)
                    )
                } else {
                    Text(text = "Generate", modifier = Modifier.padding(horizontal = 32.dp))
                }
            }

            val cards = viewState.cards

            val isRecommendationsVisible = !cards.isNullOrEmpty()

            val lazyListState = rememberLazyListState()
            AnimatedVisibility(visible = isRecommendationsVisible) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.padding(top = 16.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cards?.let {
                        items(it, key = { it.hashCode() }) { card ->
                            CardItem(cards, onCardRemoved, card, onTextChanged, onTranslationChanged, onExampleChanged)
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun ColumnScope.CardItem(
    cards: SnapshotStateList<Card>,
    onCardRemoved: (Card) -> Unit,
    card: Card,
    onTextChanged: (Card, String) -> Unit,
    onTranslationChanged: (Card, String) -> Unit,
    onExampleChanged: (Card, String) -> Unit
) {
    var isCardVisible by remember { mutableStateOf(true) }
    var isCardRemoved by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                if (cards.size > 2) {
                    isCardVisible = false
                    true
                } else {
                    false
                }
            } else {
                false
            }
        },
        positionalThreshold = { 150.dp.value }
    )

    LaunchedEffect(isCardVisible) {
        if (!isCardVisible) {
            delay(600)
            onCardRemoved.invoke(card)
        }
    }

    LaunchedEffect(isCardRemoved) {
        if (isCardRemoved) {
            dismissState.snapTo(SwipeToDismissBoxValue.EndToStart)
        }
    }

    AnimatedVisibility(visible = isCardVisible) {
        SwipeToDismissBox(
            state = dismissState,
            modifier = Modifier.padding(top = 8.dp),
            enableDismissFromEndToStart = true,
            backgroundContent = {
                val alignment = Alignment.CenterEnd

                val scale by animateFloatAsState(
                    if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
                    label = ""
                )

                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentAlignment = alignment
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Icon",
                        modifier = Modifier.scale(scale)
                    )
                }
            }, content = {
                RecommendedCard(
                    card = card,
                    onTextChanged = onTextChanged,
                    onTranslationChanged = onTranslationChanged,
                    onExampleChanged = onExampleChanged,
                    onDeleteClicked = {
                       isCardRemoved = true
                    }
                )
            })
    }
}

@Composable
private fun RecommendedCard(
    card: Card, onTextChanged: (Card, String) -> Unit,
    onTranslationChanged: (Card, String) -> Unit,
    onExampleChanged: (Card, String) -> Unit,
    onDeleteClicked: (Card) -> Unit,
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Pink80, RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            TextField(
                label = {
                    Text(text = "Term")
                },
                value = card.text,
                readOnly = false,
                onValueChange = { onTextChanged.invoke(card, it) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            TextField(
                label = {
                    Text(text = "Translation")
                },
                modifier = Modifier.padding(top = 8.dp),
                value = card.translation,
                readOnly = false,
                onValueChange = { onTranslationChanged.invoke(card, it) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            TextField(
                label = {
                    Text(text = "Example")
                },
                modifier = Modifier.padding(top = 8.dp),
                value = card.example,
                readOnly = false,
                onValueChange = { onExampleChanged.invoke(card, it) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        }
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete icon",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 16.dp)
                .clickable {
                    onDeleteClicked.invoke(card)
                }
        )
    }
}

@Preview
@Composable
fun CreateSetScreenPreview() {
    CreateSet(
        viewState = CreateSetViewState(isSetGenerating = false),
        onBackPressed = { },
        onGenerateButtonClicked = {}, {}, { _, _ -> }, { _, _ -> }, { _, _ -> },
    ) {}
}

@Preview
@Composable
fun RecommendedSetPreview() {
    RecommendedCard(card = Card(1, "text", "translation", "example", false),
        { _, _ -> }, { _, _ -> }, { _, _ -> }, {})
}