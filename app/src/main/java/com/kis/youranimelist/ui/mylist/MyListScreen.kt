package com.kis.youranimelist.ui.mylist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kis.youranimelist.NavigationKeys
import com.kis.youranimelist.R
import com.kis.youranimelist.model.app.AnimeStatusValue
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.widget.IconWithText

@Composable
fun MyListScreenRoute(
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: MyListViewModel = hiltViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()
    MyListScreen(
        isLoading = screenState.value.isLoading,
        listItems = screenState.value.items,
        tabs = screenState.value.tabs,
        currentTab = screenState.value.currentTab,
        paddingValues = paddingValues,
        onTabClicked = { tab: Int -> viewModel.onTabClicked(tab) },
        onItemClicked = { itemId: Int -> navController.navigate(NavigationKeys.Route.EXPLORE + "/$itemId") }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyListScreen(
    isLoading: Boolean,
    listItems: List<MyListScreenContract.Item>,
    tabs: List<String>,
    currentTab: Int,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onTabClicked: (Int) -> Unit,
    onItemClicked: (Int) -> Unit,
) {
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
        if (isLoading) {
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = CenterHorizontally) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(start = 8.dp,
                end = 8.dp,
                top = 8.dp,
                bottom = 0.dp), modifier = Modifier.fillMaxHeight()) {
                items(
                    items = listItems.filter { currentTab == 0 || it.status == tabs[currentTab] },
                    key = { it.id }
                ) { item ->
                    Card(
                        shape = RoundedCornerShape(10),
                        modifier = Modifier
                            .animateItemPlacement()
                            .fillMaxWidth()
                            .height(140.dp)
                            .clickable { onItemClicked.invoke(item.id) },
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
                                    .aspectRatio(0.7f),
                                contentScale = ContentScale.Crop,
                            )
                            Column(modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = item.title ?: "",
                                        style = MaterialTheme.typography.body1,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row {
                                        item.mediaType?.let { mediaType ->
                                            Text(text = if (mediaType.length < 4) mediaType.uppercase() else {
                                                mediaType
                                            },
                                                style = MaterialTheme.typography.body2)
                                        }
                                        Text(text = " | ", style = MaterialTheme.typography.body2)
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
                                                    .background(color = Theme.Colors.watchingItemColor,
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
                                            text = "${item.mean}",
                                            textStyle = MaterialTheme.typography.subtitle1,
                                            icon = R.drawable.ic_star_solid,
                                            tint = Color.Yellow,
                                            space = 4.dp,
                                        )
                                    }
                                }
                                Column(
                                ) {
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

