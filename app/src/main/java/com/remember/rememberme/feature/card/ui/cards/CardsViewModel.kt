package com.remember.rememberme.feature.card.ui.cards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remember.rememberme.core.Result
import com.remember.rememberme.core.asResult
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
import kotlinx.coroutines.flow.channelFlow
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

    private val _events = MutableSharedFlow<CardsScreenEvents>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun onSpeechRecognized(cardIndex: Int, recognizedResults: List<String>) {
        val card = cards[cardIndex]
        val result = recognizedResults.joinToString(separator = " ")

        card?.let {
            val similarityPercentage = calculateSimilarityPercentage(card.text, result)

            val isRecognitionSuccessful = similarityPercentage > 50

            _viewState.update {
                it.copy(
                    correctnessPercents = similarityPercentage.toInt(),
                    activeCardIndex = if (isRecognitionSuccessful) it.activeCardIndex + 1 else it.activeCardIndex,
                    isRecognitionSuccessful = isRecognitionSuccessful
                )
            }

            if (isRecognitionSuccessful) {
                _events.tryEmit(CardsScreenEvents.GoToNextCard)
            }

            Log.d("TAG", "similarityPercentage: $similarityPercentage of ${card.text} and $result")
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