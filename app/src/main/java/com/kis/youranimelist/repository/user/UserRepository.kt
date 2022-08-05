package com.kis.youranimelist.repository.user

import com.kis.youranimelist.model.app.Anime
import com.kis.youranimelist.model.app.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val userCache: User?

    fun updateFavoriteAnime(anime: Anime?)
    fun getUser(): Flow<User>
}
