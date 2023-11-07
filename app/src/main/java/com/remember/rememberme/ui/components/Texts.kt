package com.remember.rememberme.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Header(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier
            .padding(start = 16.dp, top = 16.dp),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        fontSize = MaterialTheme.typography.headlineMedium.fontSize
    )
}

@Composable
fun SubHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = Modifier
            .padding(
                start = 16.dp,
                top = 8.dp,
                bottom = 16.dp
            ),
        text = text,
        color = Color.White,
        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
    )

}