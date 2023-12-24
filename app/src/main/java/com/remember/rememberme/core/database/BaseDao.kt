package com.remember.rememberme.core.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.remember.rememberme.feature.card.data.database.CardEntity

interface BaseDao<T> {

    @Insert
    suspend fun insertAll(entities: List<T>)

    @Insert
    suspend fun insert(entity: T)

    @Delete
    suspend fun delete(entity: T)
}