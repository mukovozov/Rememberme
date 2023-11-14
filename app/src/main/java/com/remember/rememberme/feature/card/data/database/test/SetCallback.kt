package com.remember.rememberme.feature.card.data.database.test

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.remember.rememberme.feature.card.data.database.CardEntity
import com.remember.rememberme.feature.card.data.database.SetEntity
import com.remember.rememberme.feature.card.data.database.dao.CardDao
import com.remember.rememberme.feature.card.data.database.dao.SetDao
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
            val cards = listOf(
                CardEntity(1, "Hello", "Привет", "Hello world", 1),
                CardEntity(2, "Family bonds", "Семейные узы", "Our family bonds are unbreakable", 1),
                CardEntity(3, "Home is where the heart is", "Дом там, где сердце", "After all these travels, I've learned that home is where the heart is.", 1),
                CardEntity(4, "Family traditions", "Семейные традиции", "Passing down family traditions creates a sense of belonging.", 1),
                CardEntity(5, "Home sweet home", "Дом, милый дом", "Finally back home after a long day—home sweet home.", 1),
                CardEntity(6, "Family ties", "Семейные узы", "Despite the distance, our family ties remain strong.", 1),
            )
            val set = SetEntity(1, "set1")

            cardDaoProvider.get().insertAll(*cards.toTypedArray())
            setDaoProvider.get().insertAll(set)
        }
    }
}