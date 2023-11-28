package com.remember.rememberme.feature.card.ui.cards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remember.rememberme.core.Result
import com.remember.rememberme.core.asResult
import com.remember.rememberme.core.coroutines.EventFlow
import com.remember.rememberme.di.SetIdParameter
import com.remember.rememberme.feature.card.data.SetRepository
import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.feature.card.domain.CardsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    @SetIdParameter private val setId: Int,
    private val cardsUseCase: CardsUseCase,
    private val setsRepository: SetRepository
) : ViewModel() {

    // TODO: get rid of this and replace with a proper state or do it in use case
    private var cards: List<Card> = emptyList()

    val cardsUiState = setsRepository.getSetById(setId)
        .onEach { cards = it?.cards ?: emptyList() }
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

    private val _viewState = MutableStateFlow(CardsViewState())
    val viewState = _viewState.asStateFlow()

    private val _events = EventFlow<CardsScreenEvents>()
    val events = _events.asSharedFlow()

    fun onSpeechRecognized(cardIndex: Int, recognizedResults: List<String>) {
        val answer = recognizedResults.joinToString(separator = " ")
        onCardAnswered(cardIndex, answer)
    }

    fun onKeyboardButtonClicked(cardIndex: Int) {
        _viewState.update {
            it.copy(isInputDialogVisible = true)
        }
    }

    fun onCheckButtonClicked(cardIndex: Int) {
        // TODO: introduce "learned" logic
        onCardSkipped(cardIndex)
    }

    fun onSkipButtonClicked(cardIndex: Int) {
        onCardSkipped(cardIndex)
    }

    fun onInputConfirmed(cardIndex: Int, input: String) {
        _viewState.update {
            it.copy(isInputDialogVisible = false)
        }

        onCardAnswered(cardIndex, input)
    }

    fun onDialogDismissed() {
        _viewState.update { it.copy(isInputDialogVisible = false) }
    }

    private fun onCardSkipped(cardIndex: Int) {
        _viewState.update {
            it.copy(
                activeCardIndex = it.activeCardIndex + 1,
                isRecognitionSuccessful = false,
                score = calculateScore(false, it.score)
            )
        }

        _events.tryEmit(CardsScreenEvents.GoToNextCard)
    }

    private fun onCardAnswered(cardIndex: Int, answer: String) {
        val card = cards[cardIndex]

        val similarityPercentage = calculateSimilarityPercentage(card.text, answer)

        val isRecognitionSuccessful = similarityPercentage > 50

        _viewState.update {
            it.copy(
                correctnessPercents = similarityPercentage.toInt(),
                activeCardIndex = if (isRecognitionSuccessful) it.activeCardIndex + 1 else it.activeCardIndex,
                isRecognitionSuccessful = isRecognitionSuccessful,
                score = calculateScore(isRecognitionSuccessful, it.score),
                currentAnswer = if (!isRecognitionSuccessful) answer else ""
            )
        }

        if (isRecognitionSuccessful) {
            _events.tryEmit(CardsScreenEvents.GoToNextCard)
        }

        Log.d("TAG", "similarityPercentage: $similarityPercentage of ${card.text} and $answer")
    }

    private fun calculateScore(
        isRecognitionSuccessful: Boolean,
        currentScore: Int
    ): Int {
        // TODO: introduce more elegant formula that takes correctnessPercents in consideration
        return if (isRecognitionSuccessful) {
            currentScore + (1f / cards.size * 100).toInt()
        } else {
            currentScore
        }
    }

    private fun calculateSimilarityPercentage(str1: String, str2: String): Double {
        val distance = calculateLevenshteinDistance(str1.lowercase(), str2.lowercase())
        val maxLength = maxOf(str1.length, str2.length)
        return (1 - distance.toDouble() / maxLength) * 100
    }

    private fun calculateLevenshteinDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length

        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) {
            for (j in 0..n) {
                when {
                    i == 0 -> dp[i][j] = j
                    j == 0 -> dp[i][j] = i
                    else -> {
                        dp[i][j] = minOf(
                            dp[i - 1][j - 1] + costOfSubstitution(s1[i - 1], s2[j - 1]),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1
                        )
                    }
                }
            }
        }

        return dp[m][n]
    }

    private fun costOfSubstitution(a: Char, b: Char): Int {
        return if (a == b) 0 else 1
    }

}