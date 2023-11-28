package com.remember.rememberme.feature.card.ui.cards

import com.remember.rememberme.feature.card.data.models.CardSet

data class CardsViewState(
    val activeCardIndex: Int = 0,
    val correctnessPercents: Int = 0,
    val isRecognitionSuccessful: Boolean = false,
    val score: Int = 0,
    val isInputDialogVisible: Boolean = false,
    val currentAnswer: String = ""
)

sealed interface CardsUiState {
    object Loading : CardsUiState

    data class Success(
        val set: CardSet? = null,
    ) : CardsUiState

    object Error : CardsUiState
}