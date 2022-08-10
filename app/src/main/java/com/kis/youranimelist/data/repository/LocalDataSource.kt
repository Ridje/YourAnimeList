package com.kis.youranimelist.data.repository

import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.data.cache.model.AnimeWithPersonalStatusPersistence

interface LocalDataSource {

    suspend fun savePersonalAnimeStatusToCache(status: AnimeStatus)
    suspend fun savePersonalAnimeStatusToCache(statuses: List<AnimeStatus>)
    suspend fun getPersonalAnimeStatusFromCache(): List<AnimeWithPersonalStatusPersistence>
}
