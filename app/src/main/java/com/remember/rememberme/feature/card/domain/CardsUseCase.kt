package com.remember.rememberme.feature.card.domain

import com.remember.rememberme.feature.card.data.CardsRepository
import com.remember.rememberme.feature.card.data.models.Card
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CardsUseCase @Inject constructor(
    private val cardsRepository: CardsRepository,
) {
    operator fun invoke(setId: Int): Flow<List<Card>> {
        return cardsRepository.getCardsForSet(setId)
    }
}