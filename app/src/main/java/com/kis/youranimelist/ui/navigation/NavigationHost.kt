@file:OptIn(ExperimentalAnimationApi::class)

package com.kis.youranimelist.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.endlesslist.EndlessListScreenRoute
import com.kis.youranimelist.ui.endlesslist.EndlessListScreenSuggestionsRoute
import com.kis.youranimelist.ui.explore.ExploreScreenRoute
import com.kis.youranimelist.ui.item.ItemScreenRoute
import com.kis.youranimelist.ui.itembottomsheetdialog.ItemBottomScreenRoute
import com.kis.youranimelist.ui.login.LoginScreenRoute
import com.kis.youranimelist.ui.mylist.MyListScreenRoute
import com.kis.youranimelist.ui.profile.ProfileScreenRoute
import com.kis.youranimelist.ui.search.SearchScreenRoute
import com.kis.youranimelist.ui.settings.SettingsScreenRoute
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun YourAnimeListNavHost(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    bottomSheetNavigator: BottomSheetNavigator,
    viewModel: NavigationViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel.navigateEffects) {
        viewModel
            .navigateEffects
            .collectLatest {
                navController.navigate(NavigationKeys.Route.LOGIN) {
                    popUpTo(0)
                }
            }
    }
    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
    ) {
        AnimatedNavHost(
            navController = navController,
            startDestination = NavigationKeys.Route.LOGIN,
        ) {
            composable(
                route = NavigationKeys.Route.LOGIN
            ) {
                LoginScreenRoute(navController)
            }
            composable(
                route = NavigationKeys.Route.SEARCH,
                enterTransition = {
                    slideInHorizontally(
                        animationSpec = tween(Theme.NumberValues.screenTransitionTime),
                        initialOffsetX = { it }
                    )
                },
                exitTransition = { fadeOut(animationSpec = tween(Theme.NumberValues.screenTransitionTime)) },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(Theme.NumberValues.screenTransitionTime))
                },
                popExitTransition = {
                    slideOutHorizontally(
                        animationSpec = tween(Theme.NumberValues.screenTransitionTime),
                        targetOffsetX = { it }
                    )
                }
            ) {
                SearchScreenRoute(navController, scaffoldState)
            }
            composable(
                route = NavigationKeys.Route.EXPLORE,
            ) {
                ExploreScreenRoute(navController = navController)
            }
            composable(
                route = "${NavigationKeys.Route.EXPLORE}/{${NavigationKeys.Argument.ANIME_ID}}",
                arguments = listOf(
                    navArgument(NavigationKeys.Argument.ANIME_ID) { type = NavType.IntType }
                ),
                enterTransition = {
                    slideInVertically(
                        animationSpec = tween(Theme.NumberValues.screenTransitionTime),
                        initialOffsetY = { it }
                    )
                },
                exitTransition = {
                    slideOutVertically(
                        animationSpec = tween(Theme.NumberValues.screenTransitionTime),
                        targetOffsetY = { -it }
                    )
                },
                popEnterTransition = {
                    slideInVertically(
                        animationSpec = tween(Theme.NumberValues.screenTransitionTime),
                        initialOffsetY = { -it }
                    )
                },
                popExitTransition = {
                    slideOutVertically(
                        animationSpec = tween(Theme.NumberValues.screenTransitionTime),
                        targetOffsetY = { it }
                    )
                },
            ) {
                ItemScreenRoute(navController)
            }
            composable(route = NavigationKeys.Route.MY_LIST) {
                MyListScreenRoute(
                    navController = navController,
                    scaffoldState = scaffoldState
                )
            }
            bottomSheet(route = "${NavigationKeys.Route.MY_LIST}/{${NavigationKeys.Argument.ANIME_ID}}",
                arguments = listOf(
                    navArgument(NavigationKeys.Argument.ANIME_ID) { type = NavType.IntType }
                )) {
                ItemBottomScreenRoute(navController = navController)
            }
            composable(route = NavigationKeys.Route.PROFILE) {
                ProfileScreenRoute(navController = navController, scaffoldState = scaffoldState)
            }
            composable(route = NavigationKeys.Route.SETTINGS) {
                SettingsScreenRoute(scaffoldState = scaffoldState)
            }
            composable(
                route = "${NavigationKeys.Route.RANKING_LIST}/{${NavigationKeys.Argument.RANK}}",
                arguments = listOf(navArgument(NavigationKeys.Argument.RANK) {
                    type = NavType.StringType
                })
            ) {
                EndlessListScreenRoute(navController = navController, scaffoldState = scaffoldState)
            }
            composable(
                route = NavigationKeys.Route.SUGGESTIONS,
            ) {
                EndlessListScreenSuggestionsRoute(navController = navController,
                    scaffoldState = scaffoldState)
            }
        }
    }
}

private val Theme.NumberValues.screenTransitionTime
    get() = 500


