package com.remember.rememberme.feature.card.data

import androidx.room.withTransaction
import com.remember.rememberme.core.AppDatabase
import com.remember.rememberme.feature.card.data.database.SetWithCards
import com.remember.rememberme.feature.card.data.database.asExternalModel
import com.remember.rememberme.feature.card.data.database.dao.SetDao
import com.remember.rememberme.feature.card.data.models.CardSet
import com.remember.rememberme.feature.card.data.models.toCardEntity
import com.remember.rememberme.feature.card.data.models.toSetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SetRepository {
    fun getSets(): Flow<List<CardSet>>

    fun getSetById(setId: Int): Flow<CardSet?>

    suspend fun saveSet(set: CardSet)
}

class SetRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val setDao: SetDao,
) : SetRepository {
    override fun getSets(): Flow<List<CardSet>> {
        return setDao.getSetsWithCards()
            .map { list ->
                list.map(SetWithCards::asExternalModel)
            }
    }

    override fun getSetById(setId: Int): Flow<CardSet?> {
        return setDao.getSedWithCardsById(setId)
            .map { it?.asExternalModel() }
    }

    override suspend fun saveSet(set: CardSet) {
        val cards = set.cards.map { it.toCardEntity(set.id) }
        database.withTransaction {
            setDao.insert(set.toSetEntity())
            database.cardDao().insertAll(cards)
        }
    }
}