package com.kis.youranimelist.ui.explore

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
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
import com.kis.youranimelist.model.app.Anime
import com.kis.youranimelist.model.app.AnimeCategory
import com.kis.youranimelist.ui.widget.AnimeCategoryListItemRounded

@Composable
fun ExploreScreenRoute(
    navController: NavController,
    viewModel: ExploreViewModel = hiltViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()
    ExploreScreen(animeCategories = screenState.value.categories)
}

@Composable
fun ExploreScreen(
    animeCategories: List<AnimeCategory>,
) {
    Scaffold {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            for (category in animeCategories) {
                if (category.animeList.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()) {
                        Text(text = category.name, Modifier.padding(6.dp))
                        Text(text = "See all", Modifier.padding(6.dp))
                    }
                    Row(modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(6.dp, 6.dp)) {
                        for (anime in category.animeList) {
                            AnimeCategoryListItemRounded(anime = anime)
                            Divider(
                                color = Color.Transparent,
                                modifier = Modifier
                                    .width(12.dp)
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
    ExploreScreen(animeCategories = listOf(
        AnimeCategory("all", "test", listOf(
            Anime(
                123,
                "Boku no Hero Academia 2nd Season",
                null,
                null,
                null,
                null,
            ),
            Anime(
                124,
                "Naruto: Shippuuden",
                null,
                null,
                null,
                null,
            ),
            Anime(
                128,
                "Boku no Hero Academia 2nd Season",
                null,
                null,
                null,
                null,
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
    ))
}


