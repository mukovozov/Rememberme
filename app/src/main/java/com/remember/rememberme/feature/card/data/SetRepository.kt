package com.remember.rememberme.feature.card.data

import com.remember.rememberme.core.coroutines.DispatchersProvider
import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.feature.card.data.models.CardSet
import com.remember.rememberme.feature.card.database.dao.SetDao
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SetRepository {
    suspend fun getSets(): Result<List<CardSet>>
}

class SetRepositoryImpl @Inject constructor(
    private val setDao: SetDao,
    private val dispatchersProvider: DispatchersProvider,
) : SetRepository {
    override suspend fun getSets(): Result<List<CardSet>> {
        return withContext(dispatchersProvider.io) {
            try {
                val result = setDao.getSetsWithCards()
                    .map { setWithCards ->
                        CardSet(setWithCards.setEntity.name, setWithCards.cardsEntities.map {
                            Card(it.text, it.translation, it.example)
                        })
                    }

                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}