package com.remember.rememberme.feature.card.data.models

import com.remember.rememberme.feature.card.data.database.SetEntity

data class CardSet(
    val id: Int,
    val name: String,
    val cards: List<Card>,
    // TODO: add language
)

fun CardSet.toSetEntity(): SetEntity {
    return SetEntity(id, name)
}