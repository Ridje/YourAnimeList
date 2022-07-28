@file:OptIn(ExperimentalAnimationApi::class)

package com.kis.youranimelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.explore.ExploreScreenRoute
import com.kis.youranimelist.ui.item.ItemScreenRoute
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
        colors = colors.copy(
            surface = Theme.Colors.background,
            background = Theme.Colors.background,
            onPrimary = Theme.Colors.onPrimary,
            onBackground = Theme.Colors.onPrimary,
        ),
        typography = typography,
        shapes = shapes,
        content = content
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun YourAnimeListMainScreen() {
    val navController = rememberAnimatedNavController()
    YourAnimeListNavHost(navController)
}

@Composable
fun YourAnimeListNavHost(navController: NavHostController) {
    AnimatedNavHost(navController, startDestination = NavigationKeys.Route.LOGIN) {
        composable(route = NavigationKeys.Route.LOGIN) {
            LoginScreenRoute(navController)
        }
        composable(
            route = NavigationKeys.Route.EXPLORE,
        ) {
            ExploreScreenRoute(navController = navController)
        }
        composable(
            route = "${NavigationKeys.Route.EXPLORE}/{anime}",
            arguments = listOf(
                navArgument("anime") { type = NavType.IntType }
            ),
            enterTransition = {
                slideInVertically(
                    animationSpec = tween(500),
                    initialOffsetY = { it * 2 }
                )
            },
            popExitTransition = {
                slideOutVertically(
                    animationSpec = tween(500),
                    targetOffsetY = { (it * 1.5).toInt() }
                )
            },
        ) {
            ItemScreenRoute(navController)
        }
    }
}


object NavigationKeys {
    object Route {
        const val LOGIN = "currencies"
        const val EXPLORE = "explore"
    }
}
