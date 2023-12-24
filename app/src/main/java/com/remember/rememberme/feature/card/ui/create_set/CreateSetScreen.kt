@file:OptIn(ExperimentalMaterial3Api::class)

package com.remember.rememberme.feature.card.ui.create_set

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
        onSubmitSetButtonClicked = viewModel::onSetSubmitButtonClicked
    )
}

@Composable
fun CreateSet(
    viewState: CreateSetViewState,
    onBackPressed: () -> Unit,
    onGenerateButtonClicked: (String) -> Unit,
    onSubmitSetButtonClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Create set") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Describe below in a few words what is the theme you want to create flashcards for.")
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
        }
    }
}

@Preview
@Composable
fun CreateSetScreenPreview() {
    CreateSet(
        viewState = CreateSetViewState(isSetGenerating = false),
        onBackPressed = { },
        onGenerateButtonClicked = {}) {

    }
}