package com.remember.rememberme.feature.card.database

import androidx.room.Embedded
import androidx.room.Relation

data class SetWithCards(
    @Embedded
    val setEntity: SetEntity,
    @Relation(
        parentColumn = "setId",
        entityColumn = "card_setId"
    )
    val cardsEntities: List<CardEntity>
)