package com.remember.rememberme.feature.card.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.remember.rememberme.feature.card.ui.cards.CardsScreenRoute
import com.remember.rememberme.feature.card.ui.flashcards.FlashcardsScreen
import com.remember.rememberme.feature.card.ui.flashcards.FlashcardsScreenRoute
import com.remember.rememberme.feature.card.ui.sets.SetsScreenRoute

const val setsNavigationRoute = "sets_route"

private const val cardsNavigationRoute = "cards_route"

fun NavController.navigateToSets(navOptions: NavOptions? = null) {
    this.navigate(setsNavigationRoute, navOptions)
}

fun NavGraphBuilder.setsScreen(onSetSelected: (setId: Int) -> Unit) {
    composable(
        route = setsNavigationRoute,
    ) {
        SetsScreenRoute(onSetSelected)
    }
}

const val SET_ID_PARAMETER = "set_id_parameter"
fun NavController.navigateToCards(setId: Int) {
    navigate("$cardsNavigationRoute/$setId")
}
fun NavGraphBuilder.cardsScreen(onBackPressed: () -> Unit) {
    composable(
        route = "$cardsNavigationRoute/{$SET_ID_PARAMETER}",
        arguments = listOf(
            navArgument(SET_ID_PARAMETER) {
                type = NavType.IntType
            }
        )
    ) {
        FlashcardsScreenRoute(onBackPressed)
    }
}