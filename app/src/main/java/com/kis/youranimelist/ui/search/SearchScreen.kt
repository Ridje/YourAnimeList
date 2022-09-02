package com.kis.youranimelist.ui.search

import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kis.youranimelist.R
import com.kis.youranimelist.ui.apptopbar.SearchAnimeScreenToolbar
import com.kis.youranimelist.ui.endlesslist.EndlessListItem
import com.kis.youranimelist.ui.endlesslist.EndlessListScreenBody
import com.kis.youranimelist.ui.navigation.NavigationKeys
import kotlinx.coroutines.flow.Flow

@Composable
fun SearchScreenRoute(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: SearchScreenViewModel = hiltViewModel(),
) {
    val eventsListener = viewModel as SearchScreenContract.ScreenEventsListener
    val effectFlow = viewModel.effectFlow
    val screenState = viewModel.screenState.collectAsState()
    SearchScreen(
        listItems = screenState.value.items?.collectAsLazyPagingItems(),
        searchValue = screenState.value.searchValue,
        scaffoldState = scaffoldState,
        effectFlow = effectFlow,
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
        onItemLongPress = { itemId: Int -> navController.navigate(NavigationKeys.Route.MY_LIST + "/$itemId") },
        onSnackbarPerformedAction = { items: LazyPagingItems<EndlessListItem> ->
            eventsListener.onReloadClicked(items)
        },
        onSnackbarDismissedAction = {},
        onSearchSnackbarDismissed = {},
        onSearchSnackbarActionPerformed = {},
    )
}

@Composable
fun SearchScreen(
    listItems: LazyPagingItems<EndlessListItem>?,
    searchValue: String,
    scaffoldState: ScaffoldState,
    effectFlow: Flow<SearchScreenContract.Effect>,
    onItemClick: (Int) -> Unit,
    onItemLongPress: (Int) -> Unit,
    onNavigationIconClick: () -> Unit,
    onSearchClick: (String) -> Boolean,
    onSearchValueChanged: (String) -> Unit,
    onSnackbarPerformedAction: (LazyPagingItems<EndlessListItem>) -> Unit,
    onSnackbarDismissedAction: () -> Unit,
    onSearchSnackbarActionPerformed: () -> Unit,
    onSearchSnackbarDismissed: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            if (effect is SearchScreenContract.Effect.SearchError) {
                val snackResult = scaffoldState.snackbarHostState.showSnackbar(
                    message = context.resources.getString(R.string.search_value_is_too_short_error),
                    duration = SnackbarDuration.Short
                )
                when (snackResult) {
                    SnackbarResult.Dismissed -> onSearchSnackbarDismissed.invoke()
                    SnackbarResult.ActionPerformed -> onSearchSnackbarActionPerformed.invoke()
                }
            }
        }
    }
    Scaffold(
        topBar = {
            SearchAnimeScreenToolbar(
                searchValue,
                listItems == null,
                onNavigationIconClick,
                onSearchClick,
                onSearchValueChanged,
            )
        },
        scaffoldState = scaffoldState,
    ) {
        listItems?.let {
            EndlessListScreenBody(
                listItems,
                scaffoldState,
                onItemClick,
                onItemLongPress,
                onSnackbarPerformedAction,
                onSnackbarDismissedAction,
            )
        }
    }
}
