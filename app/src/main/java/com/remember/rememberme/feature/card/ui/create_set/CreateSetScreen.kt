@file:OptIn(ExperimentalMaterial3Api::class)

package com.remember.rememberme.feature.card.ui.create_set

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.remember.rememberme.feature.card.data.models.Card
import com.remember.rememberme.ui.theme.Black
import com.remember.rememberme.ui.theme.Purple80
import com.remember.rememberme.ui.theme.PurpleGrey40
import com.remember.rememberme.ui.theme.White

@Composable
fun CreateSetScreenRoute(
    onBackPressed: () -> Unit,
    viewModel: CreateSetViewModel = hiltViewModel(),
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    CreateSet(
        viewState = viewState,
        onBackPressed = onBackPressed,
        onGenerateButtonClicked = viewModel::onGenerateButtonClicked,
        onSubmitSetButtonClicked = viewModel::onSetSubmitButtonClicked,
        onCardChanged = viewModel::onCardChanged
    )
}

@Composable
fun CreateSet(
    viewState: CreateSetViewState,
    onBackPressed: () -> Unit,
    onGenerateButtonClicked: (String) -> Unit,
    onSubmitSetButtonClicked: () -> Unit,
    onCardChanged: (Card) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple80,
                    actionIconContentColor = Black,
                    navigationIconContentColor = Black
                ),
                title = { Text(text = "Create set", color = Black) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                },
                actions = {
                    IconButton(onClick = onSubmitSetButtonClicked) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = "")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
                .padding(it)
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Describe below in a few words what is the theme you want to create flashcards for.",
                color = White,
            )
            var input by remember { mutableStateOf("") }
            TextField(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                value = input,
                onValueChange = {
                    input = it
                },
                label = {
                    Text(text = "Theme")
                },
                placeholder = {
                    Text(text = "modern slang, something from tiktok")
                })

            Button(modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.CenterHorizontally),
                enabled = !viewState.isSetGenerating,
                onClick = {
                    onGenerateButtonClicked.invoke(input)
                }) {
                if (viewState.isSetGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically)
                    )
                } else {
                    Text(text = "Generate", modifier = Modifier.padding(horizontal = 32.dp))
                }
            }

            val cards = viewState.generatedSet?.cards ?: return@Scaffold
            LazyColumn(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cards) { card ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PurpleGrey40, RoundedCornerShape(12.dp))
                    ) {
                        TextField(
                            label = {
                                Text(text = "Term")
                            },
                            value = card.text,
                            readOnly = true,
                            onValueChange = {},
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                        TextField(
                            label = {
                                Text(text = "Translation")
                            },
                            modifier = Modifier.padding(top = 8.dp),
                            value = card.translation,
                            readOnly = true,
                            onValueChange = {},
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                        TextField(
                            label = {
                                Text(text = "Example")
                            },
                            modifier = Modifier.padding(top = 8.dp),
                            value = card.example,
                            readOnly = true,
                            onValueChange = {},
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateSetScreenPreview() {
    CreateSet(
        viewState = CreateSetViewState(isSetGenerating = false),
        onBackPressed = { },
        onGenerateButtonClicked = {}, {}) {

    }
}