package com.remember.rememberme.feature.card.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CardEntity(
    @PrimaryKey val cardId: Int,
    val text: String,
    val translation: String,
    val example: String,
    @ColumnInfo(name = "card_setId")
    val setId: Int
)