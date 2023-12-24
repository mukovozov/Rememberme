package com.remember.rememberme.feature.card.ui.create_set

import com.remember.rememberme.feature.card.data.models.CardSet

data class CreateSetViewState(
    val setTitle: String = "",
    val query: String = "",
    val isSubmitButtonEnabled: Boolean = false,
    val generatedSet: CardSet? = null,
    val isSetGenerating: Boolean = false,
)