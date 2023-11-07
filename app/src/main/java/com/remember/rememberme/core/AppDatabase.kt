package com.remember.rememberme.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.remember.rememberme.feature.card.data.database.dao.CardDao
import com.remember.rememberme.feature.card.data.database.CardEntity
import com.remember.rememberme.feature.card.data.database.SetEntity
import com.remember.rememberme.feature.card.data.database.dao.SetDao

@Database(entities = [CardEntity::class, SetEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    abstract fun setDao(): SetDao
}