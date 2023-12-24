package com.remember.rememberme.feature.card.ui.create_set

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remember.rememberme.feature.card.data.SetRepository
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
            _viewState.update { it.copy(isSetGenerating = true, query = query) }
            setCreationUseCase.createSetFromQuery(theme = query, query = query)
                .onSuccess { set ->
                    delay(2_000)
                    _viewState.update {
                        it.copy(
                            isSetGenerating = false,
                            generatedSet = set
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
}