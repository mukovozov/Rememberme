package com.remember.rememberme.feature.card.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.remember.rememberme.feature.card.ui.CardScreenRoute

const val cardsNavigationRoute = "cards_route"

fun NavController.navigateToCards(navOptions: NavOptions? = null) {
    this.navigate(cardsNavigationRoute, navOptions)
}

fun NavGraphBuilder.cardsScreen() {
    composable(
        route = cardsNavigationRoute,
    ) {
        CardScreenRoute()
    }
}