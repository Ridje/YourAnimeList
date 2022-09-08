package com.kis.youranimelist.data.repository

import androidx.room.withTransaction
import com.kis.youranimelist.core.utils.returnFinishedCatchingWithCancellation
import com.kis.youranimelist.data.cache.UserDatabase
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO
import com.kis.youranimelist.data.cache.localdatasource.AnimeLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.SideLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.SyncJobLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.UserLocalDataSource
import com.kis.youranimelist.data.cache.model.personalanime.AnimePersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimeStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.PersonalStatusOfAnimePersistence
import com.kis.youranimelist.data.cache.model.syncjob.DeferredPersonalAnimeListChange
import com.kis.youranimelist.di.Dispatcher
import com.kis.youranimelist.di.YALDispatchers
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalDataSourceImpl(
    private val database: UserDatabase,
    private val personalAnimeDAO: PersonalAnimeDAO,
    private val animeLocalDataSource: AnimeLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val sideLocalDataSource: SideLocalDataSource,
    private val syncJobLocalDataSource: SyncJobLocalDataSource,
    @Dispatcher(YALDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : LocalDataSource,
    UserLocalDataSource by userLocalDataSource,
    SideLocalDataSource by sideLocalDataSource,
    SyncJobLocalDataSource by syncJobLocalDataSource,
    AnimeLocalDataSource by animeLocalDataSource {

    override suspend fun saveAnimeWithPersonalStatusToCache(status: AnimeStatus) =
        withContext(ioDispatcher) {
            try {
                saveAnimeToCache(status.anime)
                val statusCache = AnimeStatusPersistence(status.status.presentIndex)
                val personalAnimeStatus = AnimePersonalStatusPersistence(
                    score = status.score,
                    episodesWatched = status.numWatchedEpisodes,
                    statusId = status.status.presentIndex,
                    animeId = status.anime.id,
                    updatedAt = status.updatedAt,
                )
                personalAnimeDAO.addAnimeStatus(statusCache)
                personalAnimeDAO.addPersonalAnimeStatus(personalAnimeStatus)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                } else {
                    return@withContext false
                }
            }
            return@withContext true
        }

    override suspend fun saveAnimeWithPersonalStatusToCache(statuses: List<AnimeStatus>) =
        withContext(ioDispatcher) {
            for (status in statuses) {

                saveAnimeToCache(status.anime)
                val statusCache = AnimeStatusPersistence(status.status.presentIndex)

                val personalAnimeStatus = AnimePersonalStatusPersistence(
                    score = status.score,
                    episodesWatched = status.numWatchedEpisodes,
                    statusId = status.status.presentIndex,
                    animeId = status.anime.id,
                    updatedAt = status.updatedAt,
                )

                personalAnimeDAO.addAnimeStatus(statusCache)
                personalAnimeDAO.mergeAnimePersonalStatus(personalAnimeStatus)
            }
            return@withContext true
        }

    override suspend fun deleteSyncData(): Boolean {
        return withContext(ioDispatcher) {
            returnFinishedCatchingWithCancellation {
                database.withTransaction {
                    syncJobLocalDataSource.deleteAllSyncJobs()
                    personalAnimeDAO.deleteAllPersonalStatuses()
                }
            }
        }
    }

    override suspend fun savePersonalAnimeStatusToCache(status: AnimePersonalStatusPersistence): Boolean {
        return withContext(ioDispatcher) {
            database.withTransaction {
                status.statusId?.let { personalStatusValue ->
                    personalAnimeDAO.addAnimeStatus(
                        AnimeStatusPersistence(personalStatusValue))
                }
                personalAnimeDAO.addPersonalAnimeStatus(status)
                syncJobLocalDataSource.addPersonalAnimeListSyncJob(
                    DeferredPersonalAnimeListChange(
                        status.animeId,
                        false,
                        status.updatedAt,
                    )
                )
            }

            return@withContext true
        }
    }

    override suspend fun mergePersonalAnimeStatusToCache(status: AnimePersonalStatusPersistence): Boolean {
        withContext(ioDispatcher) {
            personalAnimeDAO.mergeAnimePersonalStatus(status)
        }
        return true
    }

    override suspend fun getAnimePersonalStatus(animeId: Int): AnimePersonalStatusPersistence? {
        return withContext(ioDispatcher) {
            personalAnimeDAO.getAnimePersonalStatus(animeId)
        }
    }

    override fun getAnimeWithStatusProducerFromCache(): Flow<List<PersonalStatusOfAnimePersistence>> {
        return personalAnimeDAO.getAllAnimeWithPersonalStatuses()
    }

    override fun getAnimeWithStatusProducerFromCache(id: Int): Flow<PersonalStatusOfAnimePersistence?> {
        return personalAnimeDAO.getAnimeWithPersonalStatus(id)
    }

    override suspend fun deleteAnimePersonalStatusFromCache(animeId: Int): Boolean {
        return withContext(ioDispatcher) {
            database.withTransaction {
                personalAnimeDAO.deletePersonalAnimeStatus(animeId)
                syncJobLocalDataSource.addPersonalAnimeListSyncJob(
                    DeferredPersonalAnimeListChange(
                        animeId,
                        true,
                        System.currentTimeMillis()
                    )
                )
            }

            return@withContext true
        }
    }
}
