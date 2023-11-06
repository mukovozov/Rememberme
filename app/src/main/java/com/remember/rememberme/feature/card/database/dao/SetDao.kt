package com.remember.rememberme.feature.card.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.remember.rememberme.core.database.BaseDao
import com.remember.rememberme.feature.card.database.SetEntity
import com.remember.rememberme.feature.card.database.SetWithCards

@Dao
interface SetDao : BaseDao<SetEntity> {
    @Query("SELECT * FROM SetEntity")
    suspend fun getAll(): List<SetEntity>

    @Transaction
    @Query("SELECT * FROM SetEntity")
    suspend fun getSetsWithCards(): List<SetWithCards>
}