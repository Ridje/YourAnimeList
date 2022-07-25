package com.kis.youranimelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kis.youranimelist.ui.explore.ExploreScreenRoute
import com.kis.youranimelist.ui.login.LoginScreenRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YourAnimeListTheme {
                YourAnimeListMainScreen()
            }
        }
    }
}

@Composable
fun YourAnimeListTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

@Composable
fun YourAnimeListMainScreen() {
    val navController = rememberNavController()
    YourAnimeListNavHost(navController)
}

@Composable
fun YourAnimeListNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationKeys.Route.LOGIN) {
        composable(route = NavigationKeys.Route.LOGIN) {
            LoginScreenRoute(navController)
        }
        composable(route = NavigationKeys.Route.EXPLORE) {
            ExploreScreenRoute(navController = navController)
        }
    }
}

object NavigationKeys {
    object Route {
        const val LOGIN = "currencies"
        const val EXPLORE = "explore"
    }
}
