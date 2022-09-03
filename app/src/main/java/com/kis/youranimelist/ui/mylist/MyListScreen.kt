package com.kis.youranimelist.ui.mylist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.SnackbarDefaults
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kis.youranimelist.R
import com.kis.youranimelist.core.utils.uppercaseMediaType
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatusValue
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.Theme.NumberValues.debounceDefaultDelay
import com.kis.youranimelist.ui.Theme.NumberValues.defaultImageRatio
import com.kis.youranimelist.ui.Theme.StringValues.separator
import com.kis.youranimelist.ui.navigation.NavigationKeys
import com.kis.youranimelist.ui.widget.IconWithText
import com.kis.youranimelist.ui.widget.SimpleTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MyListScreenRoute(
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    scaffoldState: ScaffoldState,
    viewModel: MyListViewModel = hiltViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()
    val screenEventsListener = viewModel as MyListScreenContract.ScreenEventsListener
    MyListScreen(
        scaffoldState = scaffoldState,
        searchValue = screenState.value.searchValue,
        isLoading = screenState.value.isLoading,
        isError = screenState.value.isError,
        listItems = screenState.value.items.filter {
            val fitTab =
                screenState.value.currentTab == 0 || it.status == screenState.value.tabs[screenState.value.currentTab]
            val fitSearch =
                screenState.value.searchValue.isBlank() || it.title?.contains(
                    screenState.value.searchValue,
                    true) ?: false
            return@filter fitTab && fitSearch
        },
        tabs = screenState.value.tabs,
        currentTab = screenState.value.currentTab,
        paddingValues = paddingValues,
        onSwipeRefresh = screenEventsListener::onSwipeRefresh,
        onTabClicked = screenEventsListener::onTabClicked,
        onItemClicked = { itemId: Int -> navController.navigate(NavigationKeys.Route.EXPLORE + "/$itemId") },
        onItemLongPress = { itemId: Int -> navController.navigate(NavigationKeys.Route.MY_LIST + "/$itemId") },
        onSnackbarPerformedAction = screenEventsListener::onReloadClicked,
        onSnackbarDismissedAction = screenEventsListener::onResetStateClicked,
        onSearchValueChanged = screenEventsListener::onSearchValueChanged,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyListScreen(
    scaffoldState: ScaffoldState,
    searchValue: String,
    isLoading: Boolean,
    isError: Boolean,
    listItems: List<MyListScreenContract.Item>,
    tabs: List<String>,
    currentTab: Int,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onSwipeRefresh: () -> Unit,
    onTabClicked: (Int) -> Unit,
    onItemClicked: (Int) -> Unit,
    onItemLongPress: (Int) -> Unit,
    onSnackbarPerformedAction: () -> Unit,
    onSnackbarDismissedAction: () -> Unit,
    onSearchValueChanged: (String) -> Unit,
) {
    val swipeRefreshState = rememberSwipeRefreshState(isLoading)
    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = currentTab,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[currentTab]),
                )
            }) {
            for (tab in tabs.indices) {
                Tab(
                    selected = tab == currentTab,
                    onClick = { onTabClicked.invoke(tab) },
                ) {
                    Text(
                        stringArrayResource(id = R.array.personal_list_statuses)[tab].uppercase(),
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        Surface(modifier = Modifier
            .fillMaxWidth(),
            color = Color.Transparent
        ) {
            DebouncedSearch(searchValue = searchValue, onSearchValueChanged = onSearchValueChanged)
        }
        if (isError) {
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            scope.launch {
                val snackResult = scaffoldState.snackbarHostState.showSnackbar(
                    message = context.resources.getString(R.string.data_not_loaded_error),
                    actionLabel = context.resources.getString(R.string.reload_data),
                    duration = SnackbarDuration.Long
                )
                when (snackResult) {
                    SnackbarResult.Dismissed -> onSnackbarDismissedAction.invoke()
                    SnackbarResult.ActionPerformed -> onSnackbarPerformedAction.invoke()
                }
            }
        } else {
            SwipeRefresh(state = swipeRefreshState, onRefresh = { onSwipeRefresh.invoke() }) {
                if (listItems.isEmpty()) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()), contentAlignment = Center) {
                        Text(text = stringResource(R.string.list_empty),
                            modifier = Modifier.align(Center))
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(start = 8.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 0.dp), modifier = Modifier.fillMaxHeight()) {
                        items(
                            items = listItems,
                            key = { it.id }
                        ) { item ->
                            Card(
                                shape = RoundedCornerShape(Theme.NumberValues.roundedCardPercents),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .combinedClickable(onClick = { onItemClicked.invoke(item.id) },
                                        onLongClick = { onItemLongPress.invoke(item.id) }),
                                backgroundColor = Color.White.copy(alpha = 0.1f),
                                elevation = 0.dp
                            ) {
                                Row {
                                    Spacer(modifier = Modifier
                                        .width(10.dp)
                                        .fillMaxHeight()
                                        .background(color = item.color)
                                    )
                                    AsyncImage(
                                        model = item.imageUrl,
                                        contentDescription = stringResource(id = R.string.default_content_description),
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .aspectRatio(defaultImageRatio),
                                        contentScale = ContentScale.Crop,
                                    )
                                    Column(modifier = Modifier.fillMaxHeight(),
                                        verticalArrangement = Arrangement.SpaceBetween) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 8.dp,
                                                vertical = 4.dp)
                                        ) {
                                            Text(text = item.title ?: "",
                                                style = MaterialTheme.typography.body1,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Row {
                                                item.mediaType?.let { mediaType ->
                                                    Text(text = mediaType.uppercaseMediaType(),
                                                        style = MaterialTheme.typography.body2)
                                                }
                                                Text(text = separator,
                                                    style = MaterialTheme.typography.body2)
                                                Text(
                                                    text = stringArrayResource(id = R.array.personal_list_statuses)[tabs.indexOf(
                                                        item.status)],
                                                    style = MaterialTheme.typography.body2
                                                )
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(top = 4.dp)) {
                                                if (item.score != null && item.score > 0) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(color = Theme.Colors.userScore,
                                                                shape = CircleShape)
                                                            .width(26.dp)
                                                            .aspectRatio(1f),
                                                        contentAlignment = Center,
                                                    ) {
                                                        Text(text = item.score.toString(),
                                                            textAlign = TextAlign.Center,
                                                            style = MaterialTheme.typography.subtitle1)
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                }
                                                IconWithText(
                                                    text = "${item.mean ?: stringResource(id = R.string.not_rated)}",
                                                    textStyle = MaterialTheme.typography.subtitle1,
                                                    icon = R.drawable.ic_star_solid,
                                                    tint = Color.Yellow,
                                                    space = 4.dp,
                                                )
                                            }
                                        }
                                        Column {
                                            Text(text = "${item.finishedEpisodes}/${item.totalNumOfEpisodes}",
                                                style = MaterialTheme.typography.subtitle2,
                                                modifier = Modifier.padding(horizontal = 8.dp,
                                                    vertical = 4.dp))
                                            LinearProgressIndicator(modifier = Modifier
                                                .height(8.dp)
                                                .fillMaxWidth(),
                                                color = item.color,
                                                backgroundColor = AnimeStatusValue.PlanToWatch.color,
                                                progress = (item.finishedEpisodes?.toFloat()
                                                    ?: 1f) / (item.totalNumOfEpisodes?.toFloat()
                                                    ?: 1f)
                                            )
                                        }
                                    }
                                }
                            }
                            Divider(
                                color = Color.Transparent,
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(8.dp)
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DebouncedSearch(
    searchValue: String,
    onSearchValueChanged: (String) -> Unit,
) {
    var someInputText by remember {
        mutableStateOf(searchValue)
    }
    LaunchedEffect(someInputText) {
        if (someInputText.isNotBlank()) {
            delay(debounceDefaultDelay)
        }
        onSearchValueChanged.invoke(someInputText)
    }

    SimpleTextField(
        value = someInputText,
        onValueChange = { someInputText = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search_solid),
                contentDescription = stringResource(id = R.string.default_content_description),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(12.dp)
                    .aspectRatio(1f)
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            cursorColor = Color.White.copy(alpha = ContentAlpha.medium),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            leadingIconColor = contentColorFor(SnackbarDefaults.backgroundColor).copy(alpha = 0.5f),
            trailingIconColor = contentColorFor(SnackbarDefaults.backgroundColor).copy(alpha = 0.5f)
        ),
        textStyle = MaterialTheme.typography.subtitle2,
        placeholderText = stringResource(R.string.searching_in_list),
        trailingIcon = if (someInputText.isEmpty()) {
            null
        } else {
            {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cancel),
                    contentDescription = stringResource(id = R.string.default_content_description),
                    modifier = Modifier
                        .clickable { someInputText = "" }
                        .padding(horizontal = 16.dp)
                        .width(12.dp)
                        .aspectRatio(1f)
                )
            }
        }
    )
}

private val Theme.NumberValues.roundedCardPercents: Int
    get() = 10
