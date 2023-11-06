@file:OptIn(ExperimentalLayoutApi::class)

package com.remember.rememberme

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.remember.rememberme.core.AppDatabase
import com.remember.rememberme.feature.card.data.SetRepository
import com.remember.rememberme.feature.card.database.dao.CardDao
import com.remember.rememberme.feature.card.ui.navigation.navigateToCards
import com.remember.rememberme.navigation.RmNavHost
import com.remember.rememberme.navigation.TopLevelDestination
import com.remember.rememberme.ui.RmAppState
import com.remember.rememberme.ui.components.RmNavigationBar
import com.remember.rememberme.ui.components.RmNavigationBarItem
import com.remember.rememberme.ui.rememberRmAppState
import com.remember.rememberme.ui.theme.RemembermeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var database: SetRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val result = database.getSets()
            Log.d("TAG", "onCreate: $result")
        }

        setContent {
            RemembermeTheme {
                RmApp(windowSizeClass = calculateWindowSizeClass(activity = this))
            }
        }
    }
}

@Composable
fun RmApp(
    windowSizeClass: WindowSizeClass,
    appState: RmAppState = rememberRmAppState(windowSizeClass = windowSizeClass)
) {
    Scaffold(
        bottomBar = {
            RmBottomBar(
                // TODO: get from viewmodel
                destinations = appState.topLevelDestinations,
                onNavigateToDestination = { destination ->
                    when (destination) {
                        TopLevelDestination.CARDS -> {
                            appState.navController.navigateToCards()
                        }
                        else -> {
                            Log.d(TAG, "RmApp: $destination")
                        }
                    }
                },
                // TODO: get from viewModel
                currentDestination = appState.currentDestination
            )
        }
    ) { padding ->
        Row(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
        ) {
            Column(Modifier.fillMaxSize()) {
                RmNavHost(appState = appState)
            }
        }
    }
}

@Composable
private fun RmBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier
) {
    RmNavigationBar(
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            RmNavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination.invoke(destination) },
                icon = {
                    Icon(imageVector = destination.icon, contentDescription = null)
                },
                selectedIcon = {
                    Icon(imageVector = destination.selectedIcon, contentDescription = null)
                }
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RemembermeTheme {
        Greeting("Android")
    }
}