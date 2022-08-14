@file:OptIn(ExperimentalAnimationApi::class)

package com.kis.youranimelist.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.kis.youranimelist.ui.endlesslist.EndlessListScreenRoute
import com.kis.youranimelist.ui.explore.ExploreScreenRoute
import com.kis.youranimelist.ui.item.ItemScreenRoute
import com.kis.youranimelist.ui.itembottomsheetdialog.ItemBottomScreenRoute
import com.kis.youranimelist.ui.login.LoginScreenRoute
import com.kis.youranimelist.ui.mylist.MyListScreenRoute
import com.kis.youranimelist.ui.profile.ProfileScreenRoute
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun YourAnimeListNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    scaffoldState: ScaffoldState,
    bottomSheetNavigator: BottomSheetNavigator,
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
    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
    ) {
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
                MyListScreenRoute(navController = navController,
                    paddingValues = paddingValues,
                    scaffoldState = scaffoldState)
            }
            bottomSheet(route = "${NavigationKeys.Route.MY_LIST}/{${NavigationKeys.Argument.ANIME_ID}}",
                arguments = listOf(
                    navArgument(NavigationKeys.Argument.ANIME_ID) { type = NavType.IntType }
                )) {
                ItemBottomScreenRoute(navController = navController,)
            }
            composable(route = NavigationKeys.Route.PROFILE) {
                ProfileScreenRoute(navController = navController, scaffoldState = scaffoldState)
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
}

