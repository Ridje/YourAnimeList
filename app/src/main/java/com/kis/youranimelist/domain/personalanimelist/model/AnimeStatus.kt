package com.kis.youranimelist.domain.personalanimelist.model

import com.kis.youranimelist.domain.rankinglist.model.Anime

data class AnimeStatus(
    val anime: Anime,
    val status: AnimeStatusValue,
    val score: Int,
    val numWatchedEpisodes: Int,
    val updatedAt: Long,
    val tags: List<String>?,
    val comments: String?,
)
