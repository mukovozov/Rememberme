package com.remember.rememberme.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val icon: ImageVector,
) {
    MAIN(
        selectedIcon = Icons.Rounded.Home,
        icon = Icons.Outlined.Home,
    ),
    ADD(
        selectedIcon = Icons.Rounded.Add,
        icon = Icons.Outlined.Add
    ),
    PROFILE(
        selectedIcon = Icons.Rounded.Person,
        icon = Icons.Outlined.Person
    )
}