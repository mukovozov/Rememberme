package com.remember.rememberme.feature.card.ui.create_set

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remember.rememberme.feature.card.data.SetRepository
import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.feature.card.data.models.CardSet
import com.remember.rememberme.feature.create_set.domain.SetCreationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    private val _viewState = MutableStateFlow(CreateSetViewState())
    val viewState = _viewState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = CreateSetViewState()
        )

    fun onGenerateButtonClicked(query: String) {
        viewModelScope.launch {
            _viewState.update {
                it.copy(
                    isSetGenerating = true,
                    query = query,
                    generatedSet = null
                )
            }
            setCreationUseCase.createSetFromQuery(theme = query, query = query)
                .onSuccess { set ->
                    delay(2_000)
                    _viewState.update {
                        it.copy(
                            isSetGenerating = false,
                            generatedSet = set,
                            cards = set.cards.toMutableStateList()
                        )
                    }
                }
                .onError {
                    _viewState.update {
                        it.copy(isSetGenerating = false)
                    }
                }
        }
    }

    fun onSetSubmitButtonClicked() {
        viewModelScope.launch {
            val set = _viewState.value.generatedSet ?: return@launch
            setRepository.saveSet(set)
        }
    }

    fun onTextChanged(card: Card, newText: String) {
        _viewState.update { viewState ->
            val newCards = viewState.cards?.map {
                it.copy(text = newText)
            } ?: return

            viewState.copy(cards = newCards.toMutableStateList())
        }
    }

    fun onTranslationChanged(card: Card, newText: String) {
        _viewState.update { viewState ->
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
        _viewState.update { viewState ->
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
        _viewState.update {
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