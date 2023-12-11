package com.remember.rememberme.feature.card.ui.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remember.rememberme.core.Result
import com.remember.rememberme.core.asResult
import com.remember.rememberme.core.coroutines.EventFlow
import com.remember.rememberme.di.SetIdParameter
import com.remember.rememberme.feature.card.data.SetRepository
import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.feature.card.domain.CardsUseCase
import com.remember.rememberme.feature.card.ui.cards.CardsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FlashcardsViewModel @Inject constructor(
    @SetIdParameter private val setId: Int,
    private val cardsUseCase: CardsUseCase,
    private val setsRepository: SetRepository
) : ViewModel() {

    private var cards: List<Card> = emptyList()

    val cardsUiState = setsRepository.getSetById(setId)
        .filterNotNull()
        .onEach { set ->
            cards = set.cards
            _viewState.update { it.copy(currentCard = set.cards.firstOrNull()) }
        }
        .asResult()
        .map { result ->
            when (result) {
                is Result.Loading -> CardsUiState.Loading
                is Result.Error -> CardsUiState.Error
                is Result.Success -> CardsUiState.Success(result.data)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CardsUiState.Loading
        )

    private val _viewState = MutableStateFlow(FlashcardsViewState())
    val viewState = _viewState.asStateFlow()

    private val _events = EventFlow<FlashcardsScreenEvents>()
    val events = _events.asSharedFlow()

    fun onLearnedButtonClicked(cardIndex: Int) {
        nextCard(cardIndex, true)
    }

    fun onSkipButtonClicked(cardIndex: Int) {
        nextCard(cardIndex, false)
    }

    fun onRestartButtonClicked() {
        val firstCard = cards.firstOrNull() ?: return
        _viewState.update {
            it.copyWithDroppedProgress(firstCard)
        }
    }

    private fun nextCard(cardIndex: Int, isLearned: Boolean) {
        if (cardIndex == cards.size - 1) {
            _viewState.update {
                it.copy(
                    sessionProgress = 1f,
                    isSetFinished = true
                )
            }

            return
        }

        val nextCardIndex = cardIndex + 1
        _viewState.update {
            val learned = if (isLearned) it.learned + 1 else it.learned
            it.copy(
                activeCardIndex = nextCardIndex,
                currentCard = cards[nextCardIndex],
                skipped = if (isLearned) it.skipped else it.skipped + 1,
                learned = learned,
                sessionProgress = nextCardIndex.toFloat() / cards.size,
                learningProgress = learned.toFloat() / (cards.size - 1)
            )
        }

        _events.tryEmit(FlashcardsScreenEvents.GoToNextCard)
    }
}