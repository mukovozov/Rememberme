package com.remember.rememberme.feature.card.ui.flashcards

data class FlashcardsViewState(
    val activeCardIndex: Int = 0,
    val skipped: Int = 0,
    val learned: Int = 0,
)