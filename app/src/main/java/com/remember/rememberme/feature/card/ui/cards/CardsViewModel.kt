package com.remember.rememberme.feature.card.ui.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remember.rememberme.core.Result
import com.remember.rememberme.core.asResult
import com.remember.rememberme.di.SetIdParameter
import com.remember.rememberme.feature.card.domain.CardsUseCase
import com.remember.rememberme.feature.card.ui.sets.SetsUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    @SetIdParameter private val setId: Int,
    private val cardsUseCase: CardsUseCase,
) : ViewModel() {

    val cardsUiState = cardsUseCase.invoke(setId)
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
}