package com.remember.rememberme.feature.card.ui.cards

import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.feature.card.data.models.CardSet

data class CardsViewState(
    val correctnessPercents: Int? = null,
)

sealed interface CardsUiState {
    object Loading : CardsUiState

    data class Success(
        val set: CardSet? = null,
    ) : CardsUiState

    object Error : CardsUiState
}