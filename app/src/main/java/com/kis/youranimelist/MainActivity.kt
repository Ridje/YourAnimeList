@file:OptIn(ExperimentalAnimationApi::class)

package com.kis.youranimelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.bottomnavigation.BottomNavigationDestinaton
import com.kis.youranimelist.ui.bottomnavigation.MainScreenBottomNavigation
import com.kis.youranimelist.ui.navigation.YourAnimeListNavHost
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
            primary = Theme.Colors.primary,
            onSurface = Theme.Colors.onSurface,
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
            scaffoldState,
        )
    }
}

