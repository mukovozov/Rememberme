package com.remember.rememberme.feature.card.ui.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.remember.rememberme.ui.components.Header

@Composable
fun CardsScreenRoute(
    cardsViewModel: CardsViewModel = hiltViewModel(),
) {
    val cardsUiState by cardsViewModel.cardsUiState.collectAsStateWithLifecycle()
    when (val state = cardsUiState) {
        is CardsUiState.Success -> {
            Column {
                Text(text = state.cards.size.toString())
            }
        }
        else -> {
            // do nothing for now
        }
    }
}