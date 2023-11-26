package com.remember.rememberme.feature.card.data.database

import androidx.room.Embedded
import androidx.room.Relation
import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.feature.card.data.models.CardSet

data class SetWithCards(
    @Embedded
    val setEntity: SetEntity,
    @Relation(
        parentColumn = "setId",
        entityColumn = "card_setId"
    )
    val cardsEntities: List<CardEntity>
)

fun SetWithCards.asExternalModel(): CardSet = CardSet(
    id = setEntity.setId,
    name = setEntity.name,
    cards = cardsEntities.map {
        it.asExternalModel()
    }
)