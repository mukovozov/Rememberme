package com.remember.rememberme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.remember.rememberme.feature.main.navigation.mainNavigationRoute
import com.remember.rememberme.feature.main.navigation.mainScreen
import com.remember.rememberme.ui.RmAppState

@Composable
fun RmNavHost(
    appState: RmAppState,
    modifier: Modifier = Modifier,
    startDestination: String = mainNavigationRoute
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        mainScreen()
    }
}