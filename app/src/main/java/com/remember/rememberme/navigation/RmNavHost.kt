package com.remember.rememberme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.remember.rememberme.feature.card.ui.navigation.cardsNavigationRoute
import com.remember.rememberme.feature.card.ui.navigation.cardsScreen
import com.remember.rememberme.ui.RmAppState

@Composable
fun RmNavHost(
    appState: RmAppState,
    modifier: Modifier = Modifier,
    startDestination: String = cardsNavigationRoute
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        cardsScreen()
    }
}