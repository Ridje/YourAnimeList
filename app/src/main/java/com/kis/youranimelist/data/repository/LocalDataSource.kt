package com.kis.youranimelist.data.repository

import com.kis.youranimelist.data.cache.model.AnimeWithPersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.UserPersistence
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.user.model.User

interface LocalDataSource {

    suspend fun savePersonalAnimeStatusToCache(status: AnimeStatus)
    suspend fun savePersonalAnimeStatusToCache(statuses: List<AnimeStatus>)
    suspend fun getPersonalAnimeStatusFromCache(): List<AnimeWithPersonalStatusPersistence>
    suspend fun updateUserCache(user: User)
    suspend fun getUserCache(): UserPersistence?
}
