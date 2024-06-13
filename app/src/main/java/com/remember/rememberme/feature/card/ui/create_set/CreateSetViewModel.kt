package com.remember.rememberme.feature.card.ui.create_set

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remember.rememberme.feature.card.data.SetRepository
import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.feature.card.data.models.CardSet
import com.remember.rememberme.feature.create_set.domain.SetCreationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateSetViewModel @Inject constructor(
    private val setCreationUseCase: SetCreationUseCase,
    private val setRepository: SetRepository,
) : ViewModel() {

    val viewState = MutableStateFlow(CreateSetViewState())
    val events = Channel<CreateSetEvent>(capacity = Channel.BUFFERED)

    fun onGenerateButtonClicked(query: String) {
        viewModelScope.launch {
            viewState.update {
                it.copy(
                    isSetGenerating = true,
                    query = query,
                    generatedSet = null
                )
            }
            setCreationUseCase.createSetFromQuery(theme = query, query = query)
                .onSuccess { set ->
                    delay(2_000)
                    viewState.update {
                        it.copy(
                            isSetGenerating = false,
                            generatedSet = set,
                            cards = set.cards.toMutableStateList()
                        )
                    }
                }
                .onError {
                    viewState.update {
                        it.copy(isSetGenerating = false)
                    }
                }
        }
    }

    fun onSetSubmitButtonClicked() {
        viewModelScope.launch {
            val cards = viewState.value.cards?.toList()
            if (cards.isNullOrEmpty()) {
                events.send(CreateSetEvent.ShowMessage("Please add at least one card"))
                return@launch
            }
            val set = viewState.value.generatedSet?.copy(
                cards = cards
            ) ?: return@launch

            if (set.name.isEmpty()) {
                events.send(CreateSetEvent.ShowMessage("Please specify set name"))
                return@launch
            }

            setRepository.saveSet(set)
            events.send(CreateSetEvent.GoBack)
        }
    }

    fun onTextChanged(card: Card, newText: String) {
        this.viewState.update { viewState ->
            val newCards = viewState.cards?.map {
                it.copy(text = newText)
            } ?: return

            viewState.copy(cards = newCards.toMutableStateList())
        }
    }

    fun onTranslationChanged(card: Card, newText: String) {
        this.viewState.update { viewState ->
            val newCards = viewState.cards?.map {
                if (it.id == card.id) {
                    it.copy(translation = newText)
                } else {
                    it
                }
            } ?: return

            viewState.copy(cards = newCards.toMutableStateList())
        }
    }

    fun onExampleChanged(card: Card, newText: String) {
        this.viewState.update { viewState ->
            val newCards = viewState.cards?.map {
                if (it.id == card.id) {
                    it.copy(example = newText)
                } else {
                    it
                }
            } ?: return

            viewState.copy(cards = newCards.toMutableStateList())
        }
    }

    fun onCardRemoved(card: Card) {
        this.viewState.update {
            val newCards = it.cards?.minus(card) ?: return

            it.copy(cards = newCards.toMutableStateList())
        }
    }

    private fun CardSet.updateCard(card: Card, function: (Card) -> Card): CardSet {
        return copy(cards = cards.map {
            if (it.id == card.id) {
                function.invoke(it)
            } else {
                it
            }
        })
    }
}