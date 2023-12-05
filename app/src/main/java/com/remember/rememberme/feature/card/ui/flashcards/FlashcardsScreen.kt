package com.remember.rememberme.feature.card.ui.flashcards

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.remember.rememberme.R
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
    viewModel: FlashcardsViewModel = hiltViewModel(),
) {
    val cardsUiState by viewModel.cardsUiState.collectAsStateWithLifecycle()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

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
                onLearnedButtonClicked = viewModel::onLearnedButtonClicked
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
) {
    val currentCard = set.cards[viewState.activeCardIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        Header(viewState, set)

        LinearProgressIndicator(
            progress = 0.2f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

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
                        onLearnedButtonClicked.invoke(viewState.activeCardIndex)
                    },
            )

        }
    }
}

@Composable
private fun Header(
    viewState: FlashcardsViewState,
    cardSet: CardSet,
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
                .padding(8.dp),
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
    FlashcardsScreen(
        CardSet(
            1, "name", listOf(
                com.remember.rememberme.feature.card.data.models.Card(
                    1, "Hello", "Privet", "example", false
                )
            )
        ),
        FlashcardsViewState(),
        {},
        {}
    )
}
