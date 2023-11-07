package com.remember.rememberme.feature.card.data.models

data class CardSet(
    val id: Int,
    val name: String,
    val cards: List<Card>
)