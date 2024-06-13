package com.remember.rememberme.feature.card.ui.create_set

sealed interface CreateSetEvent {
    object GoBack : CreateSetEvent

    data class ShowMessage(val message: String) : CreateSetEvent
}