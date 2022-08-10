package com.kis.youranimelist.domain.user.model

import com.kis.youranimelist.domain.rankinglist.model.Anime
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
    val userAnimeStatistic: UserAnimeStatistic? = null,
)
