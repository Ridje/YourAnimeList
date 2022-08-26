package com.kis.youranimelist.ui.search

import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kis.youranimelist.ui.apptopbar.SearchAnimeScreenToolbar

@Composable
fun SearchScreenRoute(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: SearchScreenViewModel = hiltViewModel(),
) {
    val eventsListener = viewModel as SearchScreenContract.ScreenEventsListener
    val screenState = viewModel.screenState.collectAsState()
    SearchScreen(
        screenState.value.searchValue,
        scaffoldState = scaffoldState,
        onNavigationIconClick = {
            navController.popBackStack()
        },
        onSearchClick = { searchInput ->
            eventsListener.onSearchClick(searchInput)
        },
        onSearchValueChanged = { searchValue ->
            eventsListener.onSearchValueChanged(searchValue)
        }
    )
}

@Composable
fun SearchScreen(
    searchValue: String,
    scaffoldState: ScaffoldState,
    onNavigationIconClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    onSearchValueChanged: (String) -> Unit,
) {
    Scaffold(
        topBar = { SearchAnimeScreenToolbar(searchValue, onNavigationIconClick, onSearchClick, onSearchValueChanged) },
        scaffoldState = scaffoldState,
    ) {

    }
}
