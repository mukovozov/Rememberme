package com.remember.rememberme.feature.card.ui.sets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.remember.rememberme.feature.card.data.models.CardSet
import com.remember.rememberme.ui.components.Header
import com.remember.rememberme.ui.components.SubHeader

@Composable
fun SetsScreenRoute(
    onSetSelected: (setId: Int) -> Unit,
    cardsViewModel: SetsViewModel = hiltViewModel()
) {
    val cardsUiState by cardsViewModel.setsUiState.collectAsStateWithLifecycle()
    when (val state = cardsUiState) {
        is SetsUiState.Success -> {
            SetsScreen(state, onSetSelected)
        }

        is SetsUiState.Loading -> {
            CircularProgressIndicator()
        }

        is SetsUiState.Error -> {
            // do nothing yet
        }
    }
}

@Composable
private fun SetsScreen(
    state: SetsUiState.Success,
    onSetSelected: (setId: Int) -> Unit,
) {
    Column() {
        Header(text = "RememberMe")
        SubHeader(text = "Pick a set to practice")
        LazyColumn(
            modifier = Modifier
                .padding(all = 16.dp)
        ) {
            items(state.sets) { set ->
                Set(set, onSetSelected)
            }
        }
    }
}

@Composable
fun Set(
    set: CardSet,
    onSetSelected: (setId: Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                onSetSelected.invoke(set.id)
            }
    ) {
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            text = set.name,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            color = Color.White
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 32.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {

            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                text = "${set.cards.size} words"
            )

            Icon(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                imageVector = Icons.Filled.ArrowForward, contentDescription = ""
            )
        }
    }
}

@Preview
@Composable
fun SetPreview() {
    Set(CardSet(1, "Daily Conversation", listOf()), onSetSelected = { _ ->})
}