package com.kis.youranimelist.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.apptopbar.SearchAnimeToolbar
import com.kis.youranimelist.ui.navigation.NavigationKeys
import com.kis.youranimelist.ui.widget.AnimeCategoryListItemRounded
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
fun ExploreScreenRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: ExploreViewModel = hiltViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()
    ExploreScreen(
        animeCategories = screenState.value.categories,
        paddingValues = paddingValues,
        onItemClick = { animeId: Int -> navController.navigate(NavigationKeys.Route.EXPLORE + "/$animeId") },
        onRankingListClick = { rankType: String -> navController.navigate(NavigationKeys.Route.RANKING_LIST + "/$rankType") },
        onSearchClick = { navController.navigate(NavigationKeys.Route.SEARCH) }
    )
}

@Composable
fun ExploreScreen(
    animeCategories: List<ExploreScreenContract.AnimeCategory>,
    paddingValues: PaddingValues,
    onItemClick: (Int) -> Unit,
    onRankingListClick: (String) -> Unit,
    onSearchClick: () -> Unit,
) {
    CollapsingToolbarScaffold(
        state = rememberCollapsingToolbarScaffoldState(),
        toolbar = { SearchAnimeToolbar(onSearchClick = onSearchClick) },
        modifier = Modifier,
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed) {
        LazyColumn {
            items(animeCategories) { category ->
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()) {
                    Text(text = category.category.title,
                        modifier = Modifier.padding(6.dp),
                        style = MaterialTheme.typography.h6)
                    TextButton(onClick = { onRankingListClick.invoke(category.category.tag) }) {
                        Text(
                            text = "See all",
                            color = MaterialTheme.colors.onBackground,
                            style = MaterialTheme.typography.body1,
                            textDecoration = TextDecoration.Underline,
                        )
                    }
                }
                ExploreScreenCarousel(
                    lazyItems = category.pagingDataFlow.collectAsLazyPagingItems(),
                    onItemClick = onItemClick
                )
            }
            item {
                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
            }
        }
    }
}

@Composable
fun ExploreScreenCarousel(
    lazyItems: LazyPagingItems<ExploreScreenContract.AnimeCategoryItem>,
    onItemClick: (Int) -> Unit,
) {
    LazyRow(modifier = Modifier
        .wrapContentHeight()
        .height(280.dp),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
    ) {
        when (lazyItems.loadState.refresh) {
            is LoadState.Loading -> {
                items(Theme.NumberValues.previewItemsInCarousel) {
                    AnimeCategoryListItemRounded(
                        cover = null,
                        firstLine = "Loading",
                        secondLine = "",
                        showPlaceholder = true,
                        showError = false,
                    ) {}
                    Divider(
                        color = Color.Transparent,
                        modifier = Modifier
                            .width(16.dp)
                    )
                }
            }
            is LoadState.Error -> {
                items(Theme.NumberValues.previewItemsInCarousel) {
                    AnimeCategoryListItemRounded(
                        cover = null,
                        firstLine = "Error",
                        secondLine = "",
                        showPlaceholder = false,
                        showError = true,
                    ) {}
                    Divider(
                        color = Color.Transparent,
                        modifier = Modifier
                            .width(16.dp)
                    )
                }
            }
            else -> {
                items(lazyItems) { animeItem ->
                    AnimeCategoryListItemRounded(
                        cover = animeItem?.pictureUrl,
                        firstLine = animeItem?.title ?: "",
                        secondLine = "${animeItem?.year ?: ""} ${animeItem?.season ?: ""}",
                        showPlaceholder = false,
                        showError = false,
                    ) {
                        animeItem?.let { clickedAnime ->
                            onItemClick.invoke(clickedAnime.id)
                        }
                    }
                    Divider(
                        color = Color.Transparent,
                        modifier = Modifier
                            .width(16.dp)
                    )
                }
            }
        }
    }
}

private val Theme.NumberValues.previewItemsInCarousel
    get() = 6
