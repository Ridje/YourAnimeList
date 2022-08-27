package com.kis.youranimelist.ui.model

import com.kis.youranimelist.domain.rankinglist.model.AnimeCategory

object AnimeCategories {

    val animeCategories = listOf(
        AnimeCategory(AnimeRankType.TopRanked, listOf(
            null,
            null,
            null,
            null,
        )),
        AnimeCategory(AnimeRankType.Airing, listOf(
            null,
            null,
            null,
            null,
        )),
        AnimeCategory(AnimeRankType.Popular, listOf(
            null,
            null,
            null,
            null
        )),
        AnimeCategory(AnimeRankType.Upcoming, listOf(
            null,
            null,
            null,
            null
        )),
        AnimeCategory(AnimeRankType.Movies, listOf(
            null,
            null,
            null,
            null
        )),
        AnimeCategory(AnimeRankType.Favorite, listOf(
            null,
            null,
            null,
            null
        )),
    )
}

interface CachingKey {
    fun toRequest(): String
}

enum class AnimeRankType(
    val presentName: String,
    val tag: String,
): CachingKey {
    TopRanked("Top ranked", "all"),
    Airing("Airing", "airing"),
    Popular("Popular", "bypopularity"),
    Upcoming("Upcoming", "upcoming"),
    Movies("Movies", "movie"),
    Favorite("Favorite", "favorite");

    override fun toRequest(): String {
        return tag
    }
}
