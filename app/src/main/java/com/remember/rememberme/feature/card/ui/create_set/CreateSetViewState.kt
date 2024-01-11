package com.remember.rememberme.feature.card.ui.create_set

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.feature.card.data.models.CardSet
import javax.annotation.concurrent.Immutable

@Immutable
data class CreateSetViewState(
    val setTitle: String = "",
    val query: String = "",
    val isSubmitButtonEnabled: Boolean = false,
    val generatedSet: CardSet? = null,
    val cards: SnapshotStateList<Card>? = null,
    val isSetGenerating: Boolean = false,
)