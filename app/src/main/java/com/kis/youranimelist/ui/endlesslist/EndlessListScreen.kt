package com.kis.youranimelist.ui.endlesslist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.kis.youranimelist.R
import com.kis.youranimelist.core.utils.uppercaseMediaType
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.navigation.BrowseAnimeListToolbar
import com.kis.youranimelist.ui.navigation.NavigationKeys
import com.kis.youranimelist.ui.widget.IconWithText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
fun EndlessListScreenRoute(
    navController: NavController,
    scaffoldState: ScaffoldState,
    viewModel: EndlessListScreenViewModel = hiltViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()
    val screenEventsListener = viewModel as EndlessListScreenContract.ScreenEventsListener
    EndlessListScreen(
        listItems = screenState.value.items.collectAsLazyPagingItems(),
        title = screenState.value.title,
        scaffoldState,
        onItemClick = { itemId: Int -> navController.navigate(NavigationKeys.Route.EXPLORE + "/$itemId") },
        onItemLongPress = { itemId: Int -> navController.navigate(NavigationKeys.Route.MY_LIST + "/$itemId") },
        onClickBack = { navController.popBackStack() },
        onSnackbarPerformedAction = { items: LazyPagingItems<EndlessListItem> ->
            screenEventsListener.onReloadClicked(items)
        },
        onSnackbarDismissedAction = { },
    )
}

@Composable
fun EndlessListScreenSuggestionsRoute(
    navController: NavController,
    scaffoldState: ScaffoldState,
    viewModel: EndlessListScreenSuggestionsViewModel = hiltViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()
    val screenEventsListener = viewModel as EndlessListScreenContract.ScreenEventsListener
    EndlessListScreen(
        listItems = screenState.value.items.collectAsLazyPagingItems(),
        title = screenState.value.title,
        scaffoldState,
        onItemClick = { itemId: Int -> navController.navigate(NavigationKeys.Route.EXPLORE + "/$itemId") },
        onItemLongPress = { itemId: Int -> navController.navigate(NavigationKeys.Route.MY_LIST + "/$itemId") },
        onClickBack = { navController.popBackStack() },
        onSnackbarPerformedAction = { items: LazyPagingItems<EndlessListItem> ->
            screenEventsListener.onReloadClicked(items)
        },
        onSnackbarDismissedAction = { },
    )
}


@Composable
fun EndlessListScreen(
    listItems: LazyPagingItems<EndlessListItem>,
    title: String,
    scaffoldState: ScaffoldState,
    onItemClick: (Int) -> Unit,
    onItemLongPress: (Int) -> Unit,
    onClickBack: () -> Unit,
    onSnackbarPerformedAction: (LazyPagingItems<EndlessListItem>) -> Unit,
    onSnackbarDismissedAction: () -> Unit,
) {
    CollapsingToolbarScaffold(state = rememberCollapsingToolbarScaffoldState(),
        toolbar = { BrowseAnimeListToolbar(onClickBack, title) },
        modifier = Modifier,
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed) {
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

@Composable
fun EndlessListScreenBody(
    listItems: LazyPagingItems<EndlessListItem>,
    scaffoldState: ScaffoldState,
    onItemClick: (Int) -> Unit,
    onItemLongPress: (Int) -> Unit,
    onSnackbarPerformedAction: (LazyPagingItems<EndlessListItem>) -> Unit,
    onSnackbarDismissedAction: () -> Unit,
) {
    val localScope = rememberCoroutineScope()
    when (listItems.loadState.refresh) {
        LoadState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
        }
        is LoadState.Error -> {
            Column(modifier = Modifier.fillMaxSize()) {
                val context = LocalContext.current
                LaunchedEffect(listItems.loadState) {
                    localScope.launch {
                        val snackResult = scaffoldState.snackbarHostState.showSnackbar(
                            message = context.resources.getString(R.string.data_not_loaded_error),
                            actionLabel = context.resources.getString(R.string.reload_data),
                            duration = SnackbarDuration.Short,
                        )
                        when (snackResult) {
                            SnackbarResult.Dismissed -> onSnackbarDismissedAction.invoke()
                            SnackbarResult.ActionPerformed -> onSnackbarPerformedAction.invoke(
                                listItems)
                        }
                    }
                }

            }
        }
        else -> {
            EndlessListScreenBodyContent(
                listItems,
                scaffoldState,
                localScope,
                onItemClick,
                onItemLongPress,
                onSnackbarPerformedAction,
                onSnackbarDismissedAction,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EndlessListScreenBodyContent(
    listItems: LazyPagingItems<EndlessListItem>,
    scaffoldState: ScaffoldState,
    localScope: CoroutineScope,
    onItemClick: (Int) -> Unit,
    onItemLongPress: (Int) -> Unit,
    onSnackbarPerformedAction: (LazyPagingItems<EndlessListItem>) -> Unit,
    onSnackbarDismissedAction: () -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(top = 0.dp,
            bottom = 0.dp,
            start = 6.dp,
            end = 6.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(listItems) { item ->
            Card(
                modifier = Modifier
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .combinedClickable(
                        onClick = { item?.id?.let(onItemClick) },
                        onLongClick = { item?.id?.let(onItemLongPress) }
                    )
            ) {
                Row {
                    Box {
                        AsyncImage(
                            model = item?.imageUrl,
                            contentDescription = stringResource(id = R.string.default_content_description),
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxHeight()
                                .aspectRatio(Theme.NumberValues.defaultImageRatio)
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        item?.rank?.let { rank ->
                            Text(text = rank.toString(),
                                style = MaterialTheme.typography.h3,
                                modifier = Modifier
                                    .align(
                                        Alignment.BottomEnd)
                                    .clip(RoundedCornerShape(topStartPercent = 40))
                                    .background(MaterialTheme.colors.background)
                                    .padding(horizontal = 6.dp)
                            )
                        }

                    }
                    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                        Text(
                            text = item?.title ?: "",
                            style = MaterialTheme.typography.h6,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = item?.genres ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.caption,
                            softWrap = false,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconWithText(
                                text = item?.mean?.toString(),
                                textStyle = MaterialTheme.typography.body1,
                                icon = R.drawable.ic_star_solid,
                                tint = Color.Yellow,
                                space = 6.dp
                            )
                            Text(
                                text = "${item?.mediaType?.uppercaseMediaType() ?: ""}\u00A0(${item?.numEpisodes?.toString()})",
                                style = MaterialTheme.typography.body1,
                                softWrap = false,
                            )
                        }
                        Text(
                            text = item?.description
                                ?: stringResource(id = R.string.default_content_description),
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(vertical = 8.dp),
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

            }
            Divider(
                color = Color.Transparent,
                modifier = Modifier
                    .height(14.dp)
            )
        }
        if (listItems.loadState.append == LoadState.Loading) {
            item {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        if (listItems.loadState.append is LoadState.Error) {
            item {
                val context = LocalContext.current
                localScope.launch {
                    val snackResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = context.resources.getString(R.string.data_not_loaded_error),
                        actionLabel = context.resources.getString(R.string.reload_data),
                        duration = SnackbarDuration.Long
                    )
                    when (snackResult) {
                        SnackbarResult.Dismissed -> onSnackbarDismissedAction.invoke()
                        SnackbarResult.ActionPerformed -> onSnackbarPerformedAction.invoke(
                            listItems)
                    }
                }
            }
        }
    }
}

