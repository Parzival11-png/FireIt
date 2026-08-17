package com.example.fireit.ui.navigation

import androidx.compose.runtime.Composable


import androidx.navigation.compose.rememberNavController
import com.example.fireit.ui.home.HomeRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fireit.ui.settings.SettingsRoute

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "Home"
    ){
        composable("Home"){
            HomeRoute(
                onSettingsClick = {
                    navController.navigate("Settings")
                }
            )
        }
        composable("Settings"){
            SettingsRoute()
        }

    }

}