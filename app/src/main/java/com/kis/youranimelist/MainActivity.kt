@file:OptIn(ExperimentalAnimationApi::class)

package com.kis.youranimelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.kis.youranimelist.ui.NavigationViewModel
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.bottomnavigation.BottomNavigationDestinaton
import com.kis.youranimelist.ui.bottomnavigation.MainScreenBottomNavigation
import com.kis.youranimelist.ui.endlesslist.EndlessListScreenRoute
import com.kis.youranimelist.ui.explore.ExploreScreenRoute
import com.kis.youranimelist.ui.item.ItemScreenRoute
import com.kis.youranimelist.ui.login.LoginScreenRoute
import com.kis.youranimelist.ui.mylist.MyListScreenRoute
import com.kis.youranimelist.ui.profile.ProfileScreenRoute
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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
            primary = Theme.Colors.primary
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
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        bottomBar = {
            MainScreenBottomNavigation(
                navController,
                listOf(
                    BottomNavigationDestinaton.Explore,
                    BottomNavigationDestinaton.MyList,
                    BottomNavigationDestinaton.Profile,
                )
            )
        },
        scaffoldState = scaffoldState,
    ) { paddingValues ->
        YourAnimeListNavHost(
            navController,
            paddingValues,
        )
    }
}

@Composable
fun YourAnimeListNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: NavigationViewModel = hiltViewModel(),
) {
    viewModel.navigateEffects
    LaunchedEffect(viewModel.navigateEffects) {
        viewModel
            .navigateEffects
            .collectLatest {
                navController.navigate(NavigationKeys.Route.LOGIN)
            }
    }
    AnimatedNavHost(navController, startDestination = NavigationKeys.Route.LOGIN) {
        composable(
            route = NavigationKeys.Route.LOGIN
        ) {
            LoginScreenRoute(navController)
        }
        composable(
            route = NavigationKeys.Route.EXPLORE,
        ) {
            ExploreScreenRoute(navController = navController, paddingValues = paddingValues)
        }
        composable(
            route = "${NavigationKeys.Route.EXPLORE}/{${NavigationKeys.Argument.ANIME_ID}}",
            arguments = listOf(
                navArgument(NavigationKeys.Argument.ANIME_ID) { type = NavType.IntType }
            ),
            enterTransition = {
                slideInVertically(
                    animationSpec = tween(500),
                    initialOffsetY = { it }
                )
            },
            exitTransition = {
                slideOutVertically(
                    animationSpec = tween(500),
                    targetOffsetY = { -it }
                )
            },
            popEnterTransition = {
                slideInVertically(
                    animationSpec = tween(500),
                    initialOffsetY = { -it }
                )
            },
            popExitTransition = {
                slideOutVertically(
                    animationSpec = tween(500),
                    targetOffsetY = { it }
                )
            },
        ) {
            ItemScreenRoute(navController)
        }
        composable(route = NavigationKeys.Route.MY_LIST) {
            MyListScreenRoute(navController)
        }
        composable(route = NavigationKeys.Route.PROFILE) {
            ProfileScreenRoute(navController = navController)
        }
        composable(
            route = "${NavigationKeys.Route.RANKING_LIST}/{${NavigationKeys.Argument.RANK}}",
            arguments = listOf(navArgument(NavigationKeys.Argument.RANK) {
                type = NavType.StringType
            })
        ) {
            EndlessListScreenRoute(navController = navController)
        }
    }
}


object NavigationKeys {
    object Route {
        const val LOGIN = "currencies"
        const val EXPLORE = "explore"
        const val MY_LIST = "my-list"
        const val PROFILE = "profile"
        const val RANKING_LIST = "ranking"
    }

    object Argument {
        const val RANK = "rank-type"
        const val ANIME_ID = "anime"
    }
}
