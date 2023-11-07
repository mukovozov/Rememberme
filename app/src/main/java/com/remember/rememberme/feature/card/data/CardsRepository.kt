package com.remember.rememberme.feature.card.data

import com.remember.rememberme.core.database.BaseDao
import com.remember.rememberme.feature.card.data.database.CardEntity
import com.remember.rememberme.feature.card.data.database.asExternalModel
import com.remember.rememberme.feature.card.data.database.dao.CardDao
import com.remember.rememberme.feature.card.data.models.Card
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CardsRepository {
    fun getCardsForSet(setId: Int): Flow<List<Card>>
}

class CardsRepositoryImpl @Inject constructor(
    private val cardsDao: CardDao,
) : CardsRepository {
    override fun getCardsForSet(setId: Int): Flow<List<Card>> {
        return cardsDao.getCardsForSet(setId)
            .map {
                it.map(CardEntity::asExternalModel)
            }
    }
}