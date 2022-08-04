package com.kis.youranimelist.model.app

import java.util.Date

data class User(
    val id: Int,
    val name: String,
    val picture: String?,
    val gender: String?,
    val birthday: Date?,
    val location: String?,
    val joinedAt: Date?,
    val favouriteAnime: Anime? = null,
    val animeStatistics: AnimeStatistics? = null,
)
