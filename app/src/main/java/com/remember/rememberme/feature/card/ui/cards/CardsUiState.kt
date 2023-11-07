package com.remember.rememberme.feature.card.ui.cards

import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.feature.card.data.models.CardSet

sealed interface CardsUiState {
    object Loading : CardsUiState

    data class Success(
        val cards: List<Card> = emptyList()
    ) : CardsUiState

    object Error : CardsUiState
}