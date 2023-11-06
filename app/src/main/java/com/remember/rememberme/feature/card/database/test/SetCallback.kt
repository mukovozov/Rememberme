package com.remember.rememberme.feature.card.database.test

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.remember.rememberme.feature.card.database.CardEntity
import com.remember.rememberme.feature.card.database.SetEntity
import com.remember.rememberme.feature.card.database.dao.CardDao
import com.remember.rememberme.feature.card.database.dao.SetDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

class SetCallback(
    private val setDaoProvider: Provider<SetDao>,
    private val cardDaoProvider: Provider<CardDao>
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val card = CardEntity(1, "text", "translation", "example", 1)
            val set = SetEntity(1, "set1")

            cardDaoProvider.get().insertAll(card)
            setDaoProvider.get().insertAll(set)
        }
    }
}