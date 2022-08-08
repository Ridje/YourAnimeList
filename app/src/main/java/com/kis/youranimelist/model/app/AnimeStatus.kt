package com.kis.youranimelist.model.app

data class AnimeStatus(
    val anime: Anime,
    val status: AnimeStatusValue,
    val score: Int,
    val numWatchedEpisodes: Int,
)
