package com.remember.rememberme.feature.card.ui.sets

import com.remember.rememberme.feature.card.data.models.CardSet

sealed interface SetsUiState {
    object Loading : SetsUiState

    data class Success(
        val sets: List<CardSet> = emptyList()
    ) : SetsUiState

    object Error : SetsUiState
}