package com.kis.youranimelist.ui.explore

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kis.youranimelist.NavigationKeys
import com.kis.youranimelist.model.app.Anime
import com.kis.youranimelist.model.app.AnimeCategory
import com.kis.youranimelist.ui.widget.AnimeCategoryListItemRounded

@Composable
fun ExploreScreenRoute(
    navController: NavController,
    viewModel: ExploreViewModel = hiltViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()
    ExploreScreen(
        animeCategories = screenState.value.categories,
        { animeId: Int -> navController.navigate(NavigationKeys.Route.EXPLORE + "/$animeId") }
    )
}

@Composable
fun ExploreScreen(
    animeCategories: List<AnimeCategory>,
    onItemClick: (Int) -> Unit,
) {
    Scaffold { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(animeCategories) { category ->
                if (category.animeList.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()) {
                        Text(text = category.name,
                            Modifier.padding(6.dp),
                            style = MaterialTheme.typography.h6)
                        Text(text = "See all",
                            Modifier.padding(6.dp),
                            style = MaterialTheme.typography.body1)
                    }
                    Row(modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(6.dp, 6.dp)
                        .height(IntrinsicSize.Max)
                    ) {
                        for (anime in category.animeList) {
                            AnimeCategoryListItemRounded(
                                cover = anime.picture?.large,
                                firstLine = anime.title,
                                secondLine = "${anime.startSeason?.year} ${anime.startSeason?.season}"
                            ) { onItemClick.invoke(anime.id) }
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
    }
}

@Composable
@Preview

fun ExploreScreePreview() {
    ExploreScreen(
        animeCategories = listOf(
            AnimeCategory("all", "test", listOf(
                Anime(
                    123,
                    "Boku no Hero Academia 2nd Season",
                    null,
                    null,
                    null,
                    null,
                    listOf(),
                ),
                Anime(
                    124,
                    "Naruto: Shippuuden",
                    null,
                    null,
                    null,
                    null,
                    listOf(),
                ),
                Anime(
                    128,
                    "Boku no Hero Academia 2nd Season",
                    null,
                    null,
                    null,
                    null,
                    listOf(),
                ),
                Anime(
                    121,
                    "Naruto: Shippuuden",
                    null,
                    null,
                    null,
                    null,
                )
            )
            ),
            AnimeCategory("second", "test2", listOf(
                Anime(
                    126,
                    "Fullmetal Alchemist: Brotherhood",
                    null,
                    null,
                    null,
                    null,
                ),
                Anime(
                    127,
                    "Sword Art Online",
                    null,
                    null,
                    null,
                    null,
                )
            )
            )
        ),
        {},
    )
}


