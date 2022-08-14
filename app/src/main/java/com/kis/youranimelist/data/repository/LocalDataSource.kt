package com.kis.youranimelist.data.repository

import com.kis.youranimelist.data.cache.model.AnimePersonalStatusEntity
import com.kis.youranimelist.data.cache.model.AnimeWithPersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.UserPersistence
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    fun getAnimeWithStatusProducerFromCache(): Flow<List<AnimeWithPersonalStatusPersistence>>
    fun getAnimeWithStatusProducerFromCache(id: Int): Flow<AnimeWithPersonalStatusPersistence?>

    suspend fun saveAnimeWithPersonalStatusToCache(status: AnimeStatus): Boolean
    suspend fun saveAnimeWithPersonalStatusToCache(statuses: List<AnimeStatus>): Boolean
    suspend fun updateUserCache(user: User)
    suspend fun getUserCache(): UserPersistence?
    suspend fun savePersonalAnimeStatusToCache(status: AnimePersonalStatusEntity): Boolean
    suspend fun deleteAnimePersonalStatusFromCache(animeId: Int): Boolean
}
