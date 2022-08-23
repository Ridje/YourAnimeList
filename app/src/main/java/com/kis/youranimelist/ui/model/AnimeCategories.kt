package com.kis.youranimelist.ui.model

import com.kis.youranimelist.domain.rankinglist.model.AnimeCategory

object AnimeCategories {

    val animeCategories = listOf(
        AnimeCategory("Top ranked", "all", listOf(
            null,
            null,
            null,
            null,
        )),
        AnimeCategory("Airing", "airing", listOf(
            null,
            null,
            null,
            null,
        )),
        AnimeCategory("Popular", "bypopularity", listOf(
            null,
            null,
            null,
            null
        )),
        AnimeCategory("Upcoming", "upcoming", listOf(
            null,
            null,
            null,
            null
        )),
        AnimeCategory("Movies", "movie", listOf(
            null,
            null,
            null,
            null
        )),
        AnimeCategory("Favorite", "favorite", listOf(
            null,
            null,
            null,
            null
        )),
    )
}
