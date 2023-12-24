package com.remember.rememberme.feature.card.data.models

import com.remember.rememberme.feature.card.data.database.CardEntity

data class Card(
    val id: Int,
    val text: String,
    val translation: String,
    val example: String,
    val learned: Boolean,
)

fun Card.toCardEntity(setId: Int): CardEntity {
    return CardEntity(id, text, translation, example, setId)
}