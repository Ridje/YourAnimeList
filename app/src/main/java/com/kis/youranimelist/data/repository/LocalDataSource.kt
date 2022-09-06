package com.kis.youranimelist.data.repository

import com.kis.youranimelist.data.cache.localdatasource.UserLocalDataSource
import com.kis.youranimelist.data.cache.model.PicturePersistence
import com.kis.youranimelist.data.cache.model.anime.AnimeDetailedDataPersistence
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimePersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.PersonalStatusOfAnimePersistence
import com.kis.youranimelist.data.cache.model.syncjob.DeferredPersonalAnimeListChange
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.rankinglist.model.Anime
import kotlinx.coroutines.flow.Flow

interface LocalDataSource : UserLocalDataSource {

    fun getAnimeWithStatusProducerFromCache(): Flow<List<PersonalStatusOfAnimePersistence>>
    fun getAnimeWithStatusProducerFromCache(id: Int): Flow<PersonalStatusOfAnimePersistence?>
    fun getAnimeDetailedDataProducerFromCache(animeId: Int): Flow<AnimeDetailedDataPersistence?>

    suspend fun saveAnimeWithPersonalStatusToCache(status: AnimeStatus): Boolean
    suspend fun deleteSyncData(): Boolean
    suspend fun getAnimeDetailedData(animeId: Int): AnimePersistence
    suspend fun saveAnimeWithPersonalStatusToCache(statuses: List<AnimeStatus>): Boolean
    suspend fun savePersonalAnimeStatusToCache(status: AnimePersonalStatusPersistence): Boolean
    suspend fun mergePersonalAnimeStatusToCache(status: AnimePersonalStatusPersistence): Boolean
    suspend fun deleteAnimePersonalStatusFromCache(animeId: Int): Boolean
    suspend fun saveAnimeToCache(anime: Anime): Boolean
    suspend fun getAnimeMainPicture(pictureId: Long): PicturePersistence?
    suspend fun getPersonalAnimeListSyncJobs(): List<DeferredPersonalAnimeListChange>
    suspend fun removePersonalAnimeListSyncJob(deferredJob: List<DeferredPersonalAnimeListChange>): Boolean
    suspend fun removePersonalAnimeListSyncJob(animeId: Int): Boolean
    suspend fun getAnimePersonalStatus(animeId: Int): AnimePersonalStatusPersistence?
}
