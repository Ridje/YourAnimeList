package com.kis.youranimelist.data.cache.localdatasource

import androidx.room.withTransaction
import com.kis.youranimelist.core.utils.returnCatchingWithCancellation
import com.kis.youranimelist.core.utils.returnFinishedCatchingWithCancellation
import com.kis.youranimelist.data.cache.UserDatabase
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO
import com.kis.youranimelist.data.cache.model.personalanime.AnimePersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimeStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimeTagPersistence
import com.kis.youranimelist.data.cache.model.personalanime.PersonalAnimeTagsCrossRef
import com.kis.youranimelist.data.cache.model.personalanime.PersonalStatusOfAnimePersistence
import com.kis.youranimelist.data.cache.model.syncjob.DeferredPersonalAnimeListChange
import com.kis.youranimelist.di.Dispatcher
import com.kis.youranimelist.di.YALDispatchers
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PersonalAnimeLocalDataSource {
    fun getAnimeWithStatusProducer(): Flow<List<PersonalStatusOfAnimePersistence>>
    fun getAnimeWithStatusProducer(id: Int): Flow<PersonalStatusOfAnimePersistence?>

    suspend fun saveAnimeWithPersonalStatus(status: AnimeStatus): Boolean
    suspend fun saveAnimeWithPersonalStatus(statuses: List<AnimeStatus>): Boolean
    suspend fun savePersonalAnimeStatus(status: AnimePersonalStatusPersistence): Boolean
    suspend fun mergePersonalAnimeStatus(status: AnimePersonalStatusPersistence): Boolean
    suspend fun getAnimePersonalStatus(animeId: Int): AnimePersonalStatusPersistence?
    suspend fun deleteAnimePersonalStatus(animeId: Int): Boolean
    suspend fun deleteAllPersonalStatuses(): Boolean
}

class PersonalAnimeLocalDataSourceImpl @Inject constructor(
    private val database: UserDatabase,
    private val animeLocalDataSource: AnimeLocalDataSource,
    private val syncJobLocalDataSource: SyncJobLocalDataSource,
    private val personalAnimeDAO: PersonalAnimeDAO,
    @Dispatcher(YALDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : PersonalAnimeLocalDataSource {

    override fun getAnimeWithStatusProducer(): Flow<List<PersonalStatusOfAnimePersistence>> {
        return personalAnimeDAO.getAllAnimeWithPersonalStatuses()
    }

    override fun getAnimeWithStatusProducer(id: Int): Flow<PersonalStatusOfAnimePersistence?> {
        return personalAnimeDAO.getAnimeWithPersonalStatus(id)
    }

    override suspend fun saveAnimeWithPersonalStatus(status: AnimeStatus) =
        withContext(ioDispatcher) {
            returnFinishedCatchingWithCancellation {
                animeLocalDataSource.saveAnimeToCache(status.anime)
                val statusCache = AnimeStatusPersistence(status.status.presentIndex)
                val personalAnimeStatus = AnimePersonalStatusPersistence(
                    score = status.score,
                    episodesWatched = status.numWatchedEpisodes,
                    statusId = status.status.presentIndex,
                    animeId = status.anime.id,
                    updatedAt = status.updatedAt,
                    comments = status.comments,
                )
                personalAnimeDAO.addAnimeStatus(statusCache)
                personalAnimeDAO.mergeAnimePersonalStatus(personalAnimeStatus)

                val tags = status.tags?.map { AnimeTagPersistence(it) }
                tags?.let {
                    personalAnimeDAO.addAnimeTags(tags)
                    personalAnimeDAO.deletePersonalAnimeTags(personalAnimeStatus.animeId)
                }
                tags?.forEach { tag ->
                    personalAnimeDAO.addAnimePersonalAnimeTags(
                        PersonalAnimeTagsCrossRef(
                            animeId = status.anime.id,
                            tagId = tag.id,
                        )
                    )
                }
            }
        }

    override suspend fun saveAnimeWithPersonalStatus(statuses: List<AnimeStatus>) =
        withContext(ioDispatcher) {
            returnFinishedCatchingWithCancellation {
                for (status in statuses) {
                    saveAnimeWithPersonalStatus(status)
                }
            }
            return@withContext true
        }

    override suspend fun savePersonalAnimeStatus(status: AnimePersonalStatusPersistence) =
        withContext(ioDispatcher) {
            returnFinishedCatchingWithCancellation {
                database.withTransaction {
                    status.statusId?.let { personalStatusValue ->
                        personalAnimeDAO.addAnimeStatus(
                            AnimeStatusPersistence(personalStatusValue)
                        )
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
            }
        }

    override suspend fun mergePersonalAnimeStatus(status: AnimePersonalStatusPersistence) =
        withContext(ioDispatcher) {
            returnFinishedCatchingWithCancellation {
                personalAnimeDAO.mergeAnimePersonalStatus(status)
            }
        }

    override suspend fun getAnimePersonalStatus(animeId: Int): AnimePersonalStatusPersistence? {
        return withContext(ioDispatcher) {
            returnCatchingWithCancellation {
                personalAnimeDAO.getAnimePersonalStatus(animeId)
            }
        }
    }

    override suspend fun deleteAnimePersonalStatus(animeId: Int) =
        withContext(ioDispatcher) {
            returnFinishedCatchingWithCancellation {
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
            }
        }

    override suspend fun deleteAllPersonalStatuses() =
        withContext(ioDispatcher) {
            returnFinishedCatchingWithCancellation {
                personalAnimeDAO.deletePersonalAnimeTags()
                personalAnimeDAO.deleteAllPersonalStatuses()
                personalAnimeDAO.deleteTags()
            }
        }
}
