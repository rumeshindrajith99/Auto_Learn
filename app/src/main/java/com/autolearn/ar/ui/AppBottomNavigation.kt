package com.autolearn.ar.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun AppBottomNavigation(
    navController: NavHostController,
    currentRoute: String?
) {

    NavigationBar {

        // ==========================================
        // HOME
        // ==========================================

        NavigationBarItem(

            selected = currentRoute == "home",

            onClick = {

                navController.navigate("home") {

                    popUpTo(
                        navController.graph.startDestinationId
                    ) {
                        saveState = true
                    }

                    launchSingleTop = true

                    restoreState = true
                }
            },

            icon = {

                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },

            label = {

                Text("Home")
            }
        )


        // ==========================================
        // LIBRARY
        //
        // Using List because MenuBook may not be
        // available in your Compose icon version.
        // ==========================================

        NavigationBarItem(

            selected = currentRoute == "library",

            onClick = {

                navController.navigate("library") {

                    launchSingleTop = true

                    restoreState = true
                }
            },

            icon = {

                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Library"
                )
            },

            label = {

                Text("Library")
            }
        )


        // ==========================================
        // HISTORY
        //
        // Using List because History may not be
        // available in your Compose icon version.
        // ==========================================

        NavigationBarItem(

            selected = currentRoute == "history",

            onClick = {

                navController.navigate("history") {

                    launchSingleTop = true

                    restoreState = true
                }
            },

            icon = {

                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "History"
                )
            },

            label = {

                Text("History")
            }
        )


        // ==========================================
        // ACCOUNT
        // ==========================================

        NavigationBarItem(

            selected = currentRoute == "account",

            onClick = {

                navController.navigate("account") {

                    launchSingleTop = true

                    restoreState = true
                }
            },

            icon = {

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Account"
                )
            },

            label = {

                Text("Account")
            }
        )
    }
}