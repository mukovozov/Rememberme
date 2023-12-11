package com.remember.rememberme.feature.card.ui.flashcards

import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import android.util.Log
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.remember.rememberme.R
import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.feature.card.data.models.CardSet
import com.remember.rememberme.feature.card.ui.cards.CardsUiState
import com.remember.rememberme.feature.card.ui.cards.collectAsSharedFlowWithLifecycle
import com.remember.rememberme.ui.theme.Black
import com.remember.rememberme.ui.theme.LightGreen
import com.remember.rememberme.ui.theme.LightRed
import com.remember.rememberme.ui.theme.PurpleGrey40
import com.remember.rememberme.ui.theme.White

enum class CardFlippingState {
    TOP,
    DOWN,
    FLIPPING,
}

@Composable
fun FlashcardsScreenRoute(
    onBackButtonPressed: () -> Unit,
    onGoToSetsButtonClicked: () -> Unit,
    viewModel: FlashcardsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val cardsUiState by viewModel.cardsUiState.collectAsStateWithLifecycle()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    val tts: TextToSpeech = remember {
        TextToSpeech(context) { status ->
            Log.d("TAG", "FlashcardsScreenRoute: $status")
            if (status == TextToSpeech.SUCCESS) {

            }
        }
    }

    viewModel.events.collectAsSharedFlowWithLifecycle { event ->
        when (event) {
            is FlashcardsScreenEvents.GoToNextCard -> {
//                listState.animateScrollToItem(viewState.activeCardIndex)
            }
        }
    }

    when (val state = cardsUiState) {
        is CardsUiState.Success -> {
            FlashcardsScreen(
                set = state.set,
                viewState = viewState,
                onSkipButtonClicked = viewModel::onSkipButtonClicked,
                onLearnedButtonClicked = viewModel::onLearnedButtonClicked,
                onBackButtonPressed = onBackButtonPressed,
                onPlayCardClicked = {
                    tts.speak(it, QUEUE_FLUSH, null)
                },
                onRestartButtonClicked = viewModel::onRestartButtonClicked,
                onGoToSetsButtonClicked = onGoToSetsButtonClicked,
            )
        }

        is CardsUiState.Loading -> {
            // do nothing
        }

        is CardsUiState.Error -> {
            // do nothing
        }
    }
}

@Composable
fun FlashcardsScreen(
    set: CardSet,
    viewState: FlashcardsViewState,
    onSkipButtonClicked: (Int) -> Unit,
    onLearnedButtonClicked: (Int) -> Unit,
    onBackButtonPressed: () -> Unit,
    onPlayCardClicked: (String) -> Unit,
    onRestartButtonClicked: () -> Unit,
    onGoToSetsButtonClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        Header(viewState, set, onBackButtonPressed)

        LinearProgressIndicator(
            progress = viewState.sessionProgress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        if (viewState.isSetFinished) {
            Score(
                viewState = viewState,
                cardSet = set,
                onRestartButtonClicked = onRestartButtonClicked,
                onGoToSetsButtonClicked = onGoToSetsButtonClicked
            )
        } else {
            Learning(viewState, onPlayCardClicked, onSkipButtonClicked, onLearnedButtonClicked)
        }
    }
}

@Composable
fun ColumnScope.Score(
    viewState: FlashcardsViewState,
    cardSet: CardSet,
    onRestartButtonClicked: () -> Unit,
    onGoToSetsButtonClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "You're doing great! Keep focusing on your terms.",
            modifier = Modifier.fillMaxWidth(0.8f),
            color = White,
        )
    }

    Row(
        modifier = Modifier
            .padding(top = 32.dp)
            .padding(horizontal = 16.dp),
//        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val progress = viewState.learningProgress
        Box(modifier = Modifier) {
            CircularProgressIndicator(
                progress = progress,
                trackColor = LightRed,
                color = LightGreen,
                strokeWidth = 8.dp,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center)
            )

            if (progress == 1f) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    tint = LightGreen,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    contentDescription = null
                )
            } else {
                Text(
                    text = "${(progress * 100).toInt()} %",
                    modifier = Modifier.align(Alignment.Center),
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(start = 32.dp),
        ) {
            ScoreSummaryLine(title = "Know", score = viewState.learned, color = LightGreen)
            Spacer(modifier = Modifier.size(32.dp))
            ScoreSummaryLine(title = "Still learning", score = viewState.skipped, color = LightRed)
        }
    }

    Spacer(modifier = Modifier.fillMaxSize(0.75f))

    Button(
        onClick = onGoToSetsButtonClicked,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text = "Go to sets")
    }

    OutlinedButton(
        onClick = onRestartButtonClicked,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text = "Restart Flashcards")
    }

}

@Composable
private fun ScoreSummaryLine(
    title: String,
    score: Int,
    color: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = color)
        Text(
            text = score.toString(),
            color = color,
            modifier = Modifier
                .border(1.dp, color, RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ColumnScope.Learning(
    viewState: FlashcardsViewState,
    onPlayCardClicked: (String) -> Unit,
    onSkipButtonClicked: (Int) -> Unit,
    onLearnedButtonClicked: (Int) -> Unit
) {
    val currentCard = viewState.currentCard ?: return

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = viewState.learned.toString(),
            modifier = Modifier
                .padding(top = 8.dp)
                .border(1.dp, LightGreen, shape = RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50))
                .padding(vertical = 8.dp, horizontal = 16.dp),
            color = LightGreen,
        )

        Text(
            text = viewState.skipped.toString(),
            modifier = Modifier
                .padding(top = 8.dp)
                .border(1.dp, LightRed, shape = RoundedCornerShape(topStartPercent = 50, bottomStartPercent = 50))
                .padding(vertical = 8.dp, horizontal = 16.dp),
            color = LightRed,
        )
    }

    var isFlipped by remember {
        mutableStateOf(true)
    }

    var isRotated by remember {
        mutableStateOf(true)
    }

    val animationSpec = tween<Float>(200, easing = CubicBezierEasing(0.4f, 0.0f, 0.8f, 0.8f))
    val rotation: Float by animateFloatAsState(
        targetValue = if (isFlipped) 0f else 180f,
        label = "",
        animationSpec = animationSpec,
        finishedListener = {
            isRotated = !isRotated
        }
    )

    val cardFlippingState by remember {
        derivedStateOf {
            when {
                isRotated && rotation <= 90 -> CardFlippingState.TOP
                !isRotated && rotation >= 90 -> CardFlippingState.DOWN
                else -> CardFlippingState.FLIPPING
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .padding(vertical = 32.dp, horizontal = 32.dp)
            .graphicsLayer(
                rotationY = rotation,
                cameraDistance = 8 * LocalDensity.current.density
            )
            .clickable {
                isFlipped = !isFlipped
            }
            .graphicsLayer {
                if (!isRotated) {
                    rotationY = 180f
                }
            },
        colors = CardDefaults.cardColors(containerColor = PurpleGrey40)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (cardFlippingState != CardFlippingState.FLIPPING) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow, contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(16.dp)
                        .clickable {
                            onPlayCardClicked.invoke(
                                if (isFlipped) {
                                    currentCard.text
                                } else {
                                    currentCard.translation
                                }
                            )
                        }
                )
            }

            Text(
                text = when (cardFlippingState) {
                    CardFlippingState.TOP -> currentCard.text
                    CardFlippingState.DOWN -> currentCard.translation
                    CardFlippingState.FLIPPING -> ""
                },
                modifier = Modifier.align(Alignment.Center),
                fontSize = 26.sp
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_cross),
            contentDescription = null,
            tint = LightRed,
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    isFlipped = true
                    onSkipButtonClicked.invoke(viewState.activeCardIndex)
                },
        )

        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            tint = LightGreen,
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    isFlipped = true
                    onLearnedButtonClicked.invoke(viewState.activeCardIndex)
                },
        )

    }
}

@Composable
private fun Header(
    viewState: FlashcardsViewState,
    cardSet: CardSet,
    onBackButtonPressed: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            modifier = Modifier
                .size(48.dp)
                .padding(8.dp)
                .clickable { onBackButtonPressed.invoke() },
            contentDescription = null,
            tint = White,
        )

        Text(
            text = "${viewState.activeCardIndex + 1}/${cardSet.cards.size}",
            modifier = Modifier,
            fontSize = 20.sp,
            color = White,
        )

        Spacer(modifier = Modifier.size(48.dp))
    }
}


@Preview
@Composable
fun FlashcardsScreenPreview() {
    val card = Card(
        1, "Hello", "Privet", "example", false
    )

    FlashcardsScreen(
        CardSet(1, "name", listOf(card, card, card, card)),
        FlashcardsViewState(
            sessionProgress = 1f,
            isSetFinished = true,
            currentCard = card,
            learned = 1
        ),
        {},
        {},
        {},
        {},
        {},
        {}
    )
}
