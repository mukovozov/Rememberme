package com.remember.rememberme.feature.card.data.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class SetEntity(
    @PrimaryKey val setId: Int,
    val name: String,
)