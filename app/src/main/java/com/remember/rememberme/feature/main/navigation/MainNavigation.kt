package com.remember.rememberme.feature.main.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.remember.rememberme.feature.main.MainScreenRoute

const val mainNavigationRoute = "main_route"

fun NavController.navigateToForYou(navOptions: NavOptions? = null) {
    this.navigate(mainNavigationRoute, navOptions)
}

fun NavGraphBuilder.mainScreen() {
    composable(
        route = mainNavigationRoute,
    ) {
        MainScreenRoute()
    }
}