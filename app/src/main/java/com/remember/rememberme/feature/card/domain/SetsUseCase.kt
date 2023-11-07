package com.remember.rememberme.feature.card.domain

import com.remember.rememberme.feature.card.data.SetRepository
import com.remember.rememberme.feature.card.data.models.CardSet
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetsUseCase @Inject constructor(
    private val setRepository: SetRepository
) {
    operator fun invoke(): Flow<List<CardSet>> =
        setRepository.getSets()
}