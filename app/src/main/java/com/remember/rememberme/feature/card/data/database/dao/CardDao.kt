package com.remember.rememberme.feature.card.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.remember.rememberme.core.database.BaseDao
import com.remember.rememberme.feature.card.data.database.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao : BaseDao<CardEntity> {
    @Query("SELECT * FROM CardEntity")
    suspend fun getAll(): List<CardEntity>

    @Query("SELECT * FROM CardEntity WHERE card_setId=:setId")
    fun getCardsForSet(setId: Int): Flow<List<CardEntity>>

    @Query("DELETE FROM CardEntity WHERE card_setId=:setId")
    suspend fun deleteAllInsideSet(setId: Int)
}