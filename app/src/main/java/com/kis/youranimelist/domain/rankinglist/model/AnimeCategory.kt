package com.kis.youranimelist.domain.rankinglist.model

data class AnimeCategory(
    val name: String,
    val tag: String,
    val animeList: List<Anime?>,
)
