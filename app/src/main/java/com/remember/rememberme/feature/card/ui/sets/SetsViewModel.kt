package com.remember.rememberme.feature.card.ui.sets

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remember.rememberme.core.Result
import com.remember.rememberme.core.asResult
import com.remember.rememberme.feature.card.domain.SetsUseCase
import com.remember.rememberme.feature.create_set.domain.SetCreationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetsViewModel @Inject constructor(
    private val setsUseCase: SetsUseCase,
    private val setCreationUseCase: SetCreationUseCase
) : ViewModel() {

    val setsUiState: StateFlow<SetsUiState> = setsUseCase.invoke()
        .asResult()
        .map { result ->
            when (result) {
                is Result.Loading -> SetsUiState.Loading
                is Result.Error -> SetsUiState.Error
                is Result.Success -> SetsUiState.Success(result.data)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SetsUiState.Loading
        )

    private companion object {
        const val TAG = "SetsViewModel"
    }
}