package com.remember.rememberme.feature.card.ui.flashcards

import com.remember.rememberme.feature.card.data.models.Card

data class FlashcardsViewState(
    val sessionProgress: Float = 0f,
    val learningProgress: Float = 0f,
    val currentCard: Card? = null,
    val activeCardIndex: Int = 0,
    val skipped: Int = 0,
    val learned: Int = 0,
    val isSetFinished: Boolean = false,
) {
    fun copyWithDroppedProgress(firstCard: Card): FlashcardsViewState {
        return copy(
            sessionProgress = 0f,
            learningProgress = 0f,
            currentCard = firstCard,
            activeCardIndex = 0,
            skipped = 0,
            learned = 0,
            isSetFinished = false
        )
    }
}