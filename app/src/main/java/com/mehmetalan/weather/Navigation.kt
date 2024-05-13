package com.mehmetalan.weather

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mehmetalan.weather.screens.HomeScreen
import com.mehmetalan.weather.screens.SplashScreen
import com.mehmetalan.weather.screens.WeatherScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable(route = "splash") {
            SplashScreen(
                navController = navController
            )
        }
        composable(route = "home") {
            HomeScreen(
                navController = navController
            )
        }
        composable(route = "weather/{location_key}/{name}/{country}", arguments = listOf(
            navArgument("location_key") {
                type = NavType.StringType
            },
            navArgument("name") {
                type = NavType.StringType
            },
            navArgument("country") {
                type = NavType.StringType
            }
        )) {
            WeatherScreen(
                navController = navController,
                locationKey = it.arguments?.getString("location_key") ?: "",
                locationName = it.arguments?.getString("name") ?: "",
                country = it.arguments?.getString("country") ?: ""
            )
        }
    }
}