package com.remember.rememberme.feature.card.data

import com.remember.rememberme.core.coroutines.DispatchersProvider
import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.feature.card.data.models.CardSet
import com.remember.rememberme.feature.card.data.database.SetWithCards
import com.remember.rememberme.feature.card.data.database.asExternalModel
import com.remember.rememberme.feature.card.data.database.dao.SetDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SetRepository {
    fun getSets(): Flow<List<CardSet>>

    fun getSetById(setId: Int): Flow<CardSet?>
}

class SetRepositoryImpl @Inject constructor(
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
            .map {  it?.asExternalModel() }
    }
}