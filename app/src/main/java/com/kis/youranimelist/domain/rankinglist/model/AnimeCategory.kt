package com.kis.youranimelist.domain.rankinglist.model

import com.kis.youranimelist.ui.model.AnimeRankType

data class AnimeCategory(
    val rankType: AnimeRankType,
    val animeList: List<Anime?>,
)
