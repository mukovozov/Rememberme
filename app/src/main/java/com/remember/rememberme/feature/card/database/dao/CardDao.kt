package com.remember.rememberme.feature.card.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.remember.rememberme.core.database.BaseDao
import com.remember.rememberme.feature.card.database.CardEntity

@Dao
interface CardDao : BaseDao<CardEntity> {
    @Query("SELECT * FROM CardEntity")
    suspend fun getAll(): List<CardEntity>
}