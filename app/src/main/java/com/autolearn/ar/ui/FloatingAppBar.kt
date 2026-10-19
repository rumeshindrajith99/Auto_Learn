package com.autolearn.ar.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController

private data class AppTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val appTabs = listOf(
    AppTab("home", "Home", Icons.Filled.Home),
    AppTab("library", "Library", Icons.AutoMirrored.Filled.MenuBook),
    AppTab("history", "Results", Icons.Filled.History),
    AppTab("account", "Profile", Icons.Outlined.Person)
)

@Composable
fun FloatingAppBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes

    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = colors.primary,
        selectedTextColor = colors.primary,
        unselectedIconColor = colors.onSurfaceVariant,
        unselectedTextColor = colors.onSurfaceVariant,
        indicatorColor = colors.primaryContainer
    )

    Surface(
        modifier = modifier
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .fillMaxWidth()
            .shadow(10.dp, shapes.medium)
            .border(1.dp, colors.outline, shapes.medium),
        shape = shapes.medium,
        color = colors.surface,
        tonalElevation = 0.dp
    ) {
        NavigationBar(
            containerColor = colors.surface,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            appTabs.forEach { tab ->
                val selected = currentDestination
                    ?.hierarchy
                    ?.any { it.route == tab.route } == true

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(tab.route) {
                            popUpTo("home") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label
                        )
                    },
                    label = {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = itemColors
                )
            }
        }
    }
}
