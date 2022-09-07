package com.kis.youranimelist.data.repository

import com.kis.youranimelist.core.utils.returnCatchingWithCancellation
import com.kis.youranimelist.data.cache.UserDatabase
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO
import com.kis.youranimelist.data.cache.dao.SyncJobDao
import com.kis.youranimelist.data.cache.localdatasource.SideLocalDataSource
import com.kis.youranimelist.data.cache.localdatasource.UserLocalDataSource
import com.kis.youranimelist.data.cache.model.GenrePersistence
import com.kis.youranimelist.data.cache.model.PicturePersistence
import com.kis.youranimelist.data.cache.model.anime.AnimeDetailedDataPersistence
import com.kis.youranimelist.data.cache.model.anime.AnimeGenrePersistence
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence
import com.kis.youranimelist.data.cache.model.anime.RecommendedAnimePersistence
import com.kis.youranimelist.data.cache.model.anime.RelatedAnimePersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimePersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimeStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.PersonalStatusOfAnimePersistence
import com.kis.youranimelist.data.cache.model.syncjob.DeferredPersonalAnimeListChange
import com.kis.youranimelist.di.Dispatcher
import com.kis.youranimelist.di.YALDispatchers
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.rankinglist.model.Genre
import com.kis.youranimelist.domain.rankinglist.model.RecommendedAnime
import com.kis.youranimelist.domain.rankinglist.model.RelatedAnime
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalDataSourceImpl(
    private val database: UserDatabase,
    private val personalAnimeDAO: PersonalAnimeDAO,
    private val animeDAO: AnimeDAO,
    private val userLocalDataSource: UserLocalDataSource,
    private val sideLocalDataSource: SideLocalDataSource,
    private val syncJobDao: SyncJobDao,
    @Dispatcher(YALDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : LocalDataSource, UserLocalDataSource by userLocalDataSource,
    SideLocalDataSource by sideLocalDataSource {

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
            returnCatchingWithCancellation {
                database.runInTransaction {
                    syncJobDao.deleteAllSyncJobs()
                    personalAnimeDAO.deleteAllPersonalStatuses()
                }
            }
        }
    }

    override suspend fun getAnimeDetailedData(animeId: Int): AnimePersistence {
        return withContext(ioDispatcher) {
            return@withContext animeDAO.getAnimeDetailedData(animeId)
        }
    }

    override suspend fun savePersonalAnimeStatusToCache(status: AnimePersonalStatusPersistence): Boolean {
        return withContext(ioDispatcher) {
            database.runInTransaction {
                status.statusId?.let { personalStatusValue ->
                    personalAnimeDAO.addAnimeStatus(
                        AnimeStatusPersistence(personalStatusValue))
                }
                personalAnimeDAO.addPersonalAnimeStatus(status)
                syncJobDao.addPersonalAnimeListSyncJob(
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


    override suspend fun getPersonalAnimeListSyncJobs(): List<DeferredPersonalAnimeListChange> {
        return withContext(ioDispatcher) {
            syncJobDao.getPersonalAnimeListSyncJobs()
        }
    }

    override suspend fun removePersonalAnimeListSyncJob(deferredJob: List<DeferredPersonalAnimeListChange>): Boolean {
        return withContext(ioDispatcher) {
            syncJobDao.deletePersonalAnimeListSyncJob(deferredJob)
            true
        }
    }

    override suspend fun removePersonalAnimeListSyncJob(animeId: Int): Boolean {
        return withContext(ioDispatcher) {
            syncJobDao.deletePersonalAnimeListSyncJob(animeId)
            true
        }
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

    override fun getAnimeDetailedDataProducerFromCache(animeId: Int): Flow<AnimeDetailedDataPersistence?> {
        return animeDAO.getAnimeByIdObservable(animeId)
    }

    override suspend fun deleteAnimePersonalStatusFromCache(animeId: Int): Boolean {
        return withContext(ioDispatcher) {
            database.runInTransaction {
                personalAnimeDAO.deletePersonalAnimeStatus(animeId)
                syncJobDao.addPersonalAnimeListSyncJob(
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

    override suspend fun saveAnimeToCache(anime: Anime): Boolean {
        return withContext(ioDispatcher) {
            try {
                val startSeasonId = sideLocalDataSource.getOrCreateSeason(
                    anime.startSeason?.year,
                    anime.startSeason?.season
                )?.id

                val mainPictureId = sideLocalDataSource.saveAnimeMainPicture(anime.id,
                    PicturePersistence(
                        0,
                        null,
                        anime.picture?.large,
                        anime.picture?.medium
                    )
                )

                animeDAO.addAnime(
                    AnimePersistence(
                        id = anime.id,
                        title = anime.title,
                        numEpisodes = anime.numEpisodes,
                        synopsis = anime.synopsis,
                        mean = anime.mean,
                        mediaType = anime.mediaType,
                        pictureId = mainPictureId,
                        startSeasonId = startSeasonId,
                        airingStatus = anime.airingStatus
                    )
                )

                if (anime.relatedAnime.isNotEmpty()) {
                    saveRelatedAnime(anime, anime.relatedAnime)
                }

                if (anime.recommendedAnime.isNotEmpty()) {
                    saveRecommendedAnime(anime, anime.recommendedAnime)
                }

                if (anime.pictures.isNotEmpty()) {
                    saveAnimePictures(
                        animeId = anime.id,
                        pictures = anime.pictures.map {
                            PicturePersistence(
                                0,
                                anime.id,
                                it.large,
                                it.medium,
                            )
                        }
                    )
                }

                if (anime.genres.isNotEmpty()) {
                    saveAnimeGenres(anime, anime.genres)
                }

                return@withContext true
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                return@withContext false
            }
        }
    }

    suspend fun saveAnimeGenres(anime: Anime, genres: List<Genre>) =
        withContext(ioDispatcher) {
            sideLocalDataSource.saveGenres(genres.map { GenrePersistence(it.id, it.name) })
            for (genre in genres) {
                animeDAO.addAnimeGenre(
                    AnimeGenrePersistence(
                        anime.id, genre.id
                    )
                )
            }
        }

    suspend fun saveRelatedAnime(anime: Anime, relatedAnimeList: List<RelatedAnime>) =
        withContext(ioDispatcher) {
            for (relatedAnime in relatedAnimeList) {
                if (!animeDAO.isAnimeRecordExist(relatedAnime.anime.id)) {
                    saveAnimeToCache(relatedAnime.anime)
                }
                animeDAO.addRelatedAnime(
                    RelatedAnimePersistence(
                        anime.id,
                        relatedAnime.anime.id,
                        relatedAnime.relatedTypeFormatted,
                        relatedAnime.relatedType,
                    )
                )
            }
        }

    suspend fun saveRecommendedAnime(anime: Anime, recommendedAnimeList: List<RecommendedAnime>) =
        withContext(ioDispatcher) {
            for (recommendedAnime in recommendedAnimeList) {
                if (!animeDAO.isAnimeRecordExist(recommendedAnime.anime.id)) {
                    saveAnimeToCache(recommendedAnime.anime)
                }
                animeDAO.addRecommendedAnime(
                    RecommendedAnimePersistence(
                        anime.id,
                        recommendedAnime.anime.id,
                        recommendedAnime.recommendedTimes
                    )
                )
            }
        }
}
