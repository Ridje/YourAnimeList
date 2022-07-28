package com.kis.youranimelist.model.app

data class AnimeCategory(
    val name: String,
    val tag: String,
    val animeList: List<Anime>,
)
