package com.remember.rememberme.feature.card.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.remember.rememberme.feature.card.data.models.Card

@Entity
data class CardEntity(
    @PrimaryKey val cardId: Int,
    val text: String,
    val translation: String,
    val example: String,
    @ColumnInfo(name = "card_setId")
    val setId: Int
)

fun CardEntity.asExternalModel(): Card {
    return Card(text, translation, example)
}