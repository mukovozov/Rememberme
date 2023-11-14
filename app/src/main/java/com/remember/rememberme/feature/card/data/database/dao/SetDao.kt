package com.remember.rememberme.feature.card.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.remember.rememberme.core.database.BaseDao
import com.remember.rememberme.feature.card.data.database.SetEntity
import com.remember.rememberme.feature.card.data.database.SetWithCards
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao : BaseDao<SetEntity> {
    @Query("SELECT * FROM SetEntity")
    suspend fun getAll(): List<SetEntity>

    @Transaction
    @Query("SELECT * FROM SetEntity")
    fun getSetsWithCards(): Flow<List<SetWithCards>>

    @Transaction
    @Query("SELECT * FROM SetEntity WHERE setId=:setId")
    fun getSedWithCardsById(setId: Int): Flow<SetWithCards?>
}