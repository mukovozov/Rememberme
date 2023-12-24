package com.remember.rememberme.feature.card.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.remember.rememberme.feature.card.ui.create_set.CreateSetScreenRoute
import com.remember.rememberme.feature.card.ui.flashcards.FlashcardsScreenRoute
import com.remember.rememberme.feature.card.ui.sets.SetsScreenRoute

const val setsNavigationRoute = "sets_route"
const val createSetNavigationRoute = "create_set_route"
private const val cardsNavigationRoute = "cards_route"

fun NavController.navigateToSets() {
    val navOptions = NavOptions.Builder()
        .setPopUpTo(setsNavigationRoute, inclusive = true)
        .build()

    this.navigate(setsNavigationRoute, navOptions)
}

fun NavController.navigateToCreateSetScreen() {
    navigate(createSetNavigationRoute)
}

fun NavGraphBuilder.setsScreen(
    onSetSelected: (setId: Int) -> Unit,
    onCreateSetClicked: () -> Unit,
) {
    composable(
        route = setsNavigationRoute,
    ) {
        SetsScreenRoute(onSetSelected, onCreateSetClicked)
    }
}

fun NavGraphBuilder.createSetScreen(onBackPressed: () -> Unit) {
    composable(
        route = createSetNavigationRoute
    ) {
        CreateSetScreenRoute(onBackPressed = onBackPressed)
    }
}

const val SET_ID_PARAMETER = "set_id_parameter"
fun NavController.navigateToCards(setId: Int) {
    navigate("$cardsNavigationRoute/$setId")
}

fun NavGraphBuilder.cardsScreen(
    onBackPressed: () -> Unit,
    onGoToSetsButtonClicked: () -> Unit,
) {
    composable(
        route = "$cardsNavigationRoute/{$SET_ID_PARAMETER}",
        arguments = listOf(
            navArgument(SET_ID_PARAMETER) {
                type = NavType.IntType
            }
        )
    ) {
        FlashcardsScreenRoute(onBackPressed, onGoToSetsButtonClicked)
    }
}