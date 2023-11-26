package com.remember.rememberme.feature.card.data.models

data class Card(
    val id: Int,
    val text: String,
    val translation: String,
    val example: String,
    val learned: Boolean,
)