package com.kis.youranimelist.ui.search

import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kis.youranimelist.ui.apptopbar.SearchAnimeScreenToolbar
import com.kis.youranimelist.ui.endlesslist.EndlessListScreenBody
import com.kis.youranimelist.ui.endlesslist.Item
import com.kis.youranimelist.ui.navigation.NavigationKeys

@Composable
fun SearchScreenRoute(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: SearchScreenViewModel = hiltViewModel(),
) {
    val eventsListener = viewModel as SearchScreenContract.ScreenEventsListener
    val screenState = viewModel.screenState.collectAsState()
    SearchScreen(
        listItems = screenState.value.items?.collectAsLazyPagingItems(),
        searchValue = screenState.value.searchValue,
        scaffoldState = scaffoldState,
        onNavigationIconClick = {
            navController.popBackStack()
        },
        onSearchClick = { searchInput ->
            eventsListener.onSearchClick(searchInput)
        },
        onSearchValueChanged = { searchValue ->
            eventsListener.onSearchValueChanged(searchValue)
        },
        onItemClick = { itemId: Int -> navController.navigate(NavigationKeys.Route.EXPLORE + "/$itemId") },
        onSnackbarPerformedAction = { items: LazyPagingItems<Item> ->
            eventsListener.onReloadClicked(items)
        },
        onSnackbarDismissedAction = { navController.popBackStack() },
    )
}

@Composable
fun SearchScreen(
    listItems: LazyPagingItems<Item>?,
    searchValue: String,
    scaffoldState: ScaffoldState,
    onItemClick: (Int) -> Unit,
    onNavigationIconClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    onSearchValueChanged: (String) -> Unit,
    onSnackbarPerformedAction: (LazyPagingItems<Item>) -> Unit,
    onSnackbarDismissedAction: () -> Unit,
) {
    Scaffold(
        topBar = {
            SearchAnimeScreenToolbar(searchValue,
                onNavigationIconClick,
                onSearchClick,
                onSearchValueChanged)
        },
        scaffoldState = scaffoldState,
    ) {
        listItems?.let {
            EndlessListScreenBody(
                listItems,
                scaffoldState,
                onItemClick,
                onSnackbarPerformedAction,
                onSnackbarDismissedAction,
            )
        }

    }
}
