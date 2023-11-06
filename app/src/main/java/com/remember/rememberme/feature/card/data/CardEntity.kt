package com.remember.rememberme.feature.card.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class CardEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "translation") val translation: String,
    @ColumnInfo(name = "example") val example: String,
)