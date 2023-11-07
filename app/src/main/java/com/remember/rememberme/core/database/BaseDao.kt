package com.remember.rememberme.core.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.remember.rememberme.feature.card.data.database.CardEntity

interface BaseDao<T> {

    @Insert
    suspend fun insertAll(vararg entities: T)

    @Delete
    suspend fun delete(entity: T)
}