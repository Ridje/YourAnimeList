package com.kis.youranimelist.data.repository.user

import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val userCache: User?

    fun updateFavoriteAnime(anime: Anime?)
    fun getUser(): Flow<User>
}
