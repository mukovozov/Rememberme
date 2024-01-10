package com.remember.rememberme.feature.create_set.domain

import com.remember.rememberme.core.coroutines.Outcome
import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.feature.card.data.models.CardSet
import com.remember.rememberme.feature.create_set.data.SetCreationRepository
import java.util.UUID
import javax.inject.Inject

class SetCreationUseCase @Inject constructor(
    private val repository: SetCreationRepository,
) {
    suspend fun createSetFromQuery(theme: String, query: String): Outcome<CardSet> {
        return repository.createFromQuery(query)
            .map { response ->
                CardSet(
                    id = UUID.randomUUID().hashCode(),
                    name = theme,
                    cards = parseResponse(response.content)
                )
            }
    }

    private fun parseResponse(content: String): List<Card> {
        val formattedContent = content.split("\n")
            .filter { it.startsWith("&&") }

        return formattedContent
            .map { it.replace("&&", "") }
            .map { content ->
                content.split("^")
                    .filter { it.isNotEmpty() }
            }
            .filter { it.size == 3 }
            .map {
                Card(
                    id = UUID.randomUUID().hashCode(),
                    text = it[0].trim(),
                    translation = it[1].trim(),
                    example = it[2].trim(),
                    learned = false
                )
            }
    }
}