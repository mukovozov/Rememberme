package com.remember.rememberme.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.remember.rememberme.feature.card.database.dao.CardDao
import com.remember.rememberme.feature.card.database.CardEntity
import com.remember.rememberme.feature.card.database.SetEntity
import com.remember.rememberme.feature.card.database.dao.SetDao

@Database(entities = [CardEntity::class, SetEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    abstract fun setDao(): SetDao
}