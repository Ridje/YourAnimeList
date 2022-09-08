package com.kis.youranimelist.data.repository

import com.kis.youranimelist.data.cache.localdatasource.AnimeLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.SideLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.SyncJobLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.UserLocalDataSource
import com.kis.youranimelist.data.cache.model.personalanime.AnimePersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.PersonalStatusOfAnimePersistence
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import kotlinx.coroutines.flow.Flow

interface LocalDataSource : UserLocalDataSource, SideLocalDataSource, SyncJobLocalDataSource,
    AnimeLocalDataSource {

    fun getAnimeWithStatusProducerFromCache(): Flow<List<PersonalStatusOfAnimePersistence>>
    fun getAnimeWithStatusProducerFromCache(id: Int): Flow<PersonalStatusOfAnimePersistence?>

    suspend fun saveAnimeWithPersonalStatusToCache(status: AnimeStatus): Boolean
    suspend fun deleteSyncData(): Boolean
    suspend fun saveAnimeWithPersonalStatusToCache(statuses: List<AnimeStatus>): Boolean
    suspend fun savePersonalAnimeStatusToCache(status: AnimePersonalStatusPersistence): Boolean
    suspend fun mergePersonalAnimeStatusToCache(status: AnimePersonalStatusPersistence): Boolean
    suspend fun deleteAnimePersonalStatusFromCache(animeId: Int): Boolean
    suspend fun getAnimePersonalStatus(animeId: Int): AnimePersonalStatusPersistence?
}
