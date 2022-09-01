package com.kis.youranimelist.data.repository

import androidx.room.Transaction
import com.kis.youranimelist.data.cache.UserDatabase
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO
import com.kis.youranimelist.data.cache.dao.SideDAO
import com.kis.youranimelist.data.cache.dao.SyncJobDao
import com.kis.youranimelist.data.cache.dao.UserDAO
import com.kis.youranimelist.data.cache.model.GenrePersistence
import com.kis.youranimelist.data.cache.model.PicturePersistence
import com.kis.youranimelist.data.cache.model.UserPersistence
import com.kis.youranimelist.data.cache.model.anime.AnimeDetailedDataPersistence
import com.kis.youranimelist.data.cache.model.anime.AnimeGenrePersistence
import com.kis.youranimelist.data.cache.model.anime.AnimePersistence
import com.kis.youranimelist.data.cache.model.anime.RecommendedAnimePersistence
import com.kis.youranimelist.data.cache.model.anime.RelatedAnimePersistence
import com.kis.youranimelist.data.cache.model.anime.SeasonPersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimePersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.AnimeStatusPersistence
import com.kis.youranimelist.data.cache.model.personalanime.PersonalStatusOfAnimePersistence
import com.kis.youranimelist.data.cache.model.syncjob.DeferredPersonalAnimeListChange
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.rankinglist.model.Genre
import com.kis.youranimelist.domain.rankinglist.model.Picture
import com.kis.youranimelist.domain.rankinglist.model.RecommendedAnime
import com.kis.youranimelist.domain.rankinglist.model.RelatedAnime
import com.kis.youranimelist.domain.user.mapper.UserMapper
import com.kis.youranimelist.domain.user.model.User
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalDataSourceImpl(
    private val database: UserDatabase,
    private val personalAnimeDAO: PersonalAnimeDAO,
    private val animeDAO: AnimeDAO,
    private val userDAO: UserDAO,
    private val sideDAO: SideDAO,
    private val syncJobDao: SyncJobDao,
    private val dispatchers: Dispatchers,
) : LocalDataSource {

    override suspend fun saveAnimeWithPersonalStatusToCache(status: AnimeStatus) =
        withContext(dispatchers.IO) {
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

    @Transaction
    override suspend fun saveAnimeWithPersonalStatusToCache(statuses: List<AnimeStatus>) =
        withContext(dispatchers.IO) {
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

    override suspend fun getAnimeDetailedData(animeId: Int): AnimePersistence {
        return withContext(dispatchers.IO) {
            try {
                return@withContext animeDAO.getAnimeDetailedData(animeId)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun savePersonalAnimeStatusToCache(status: AnimePersonalStatusPersistence): Boolean {
        return withContext(dispatchers.IO) {
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
        withContext(dispatchers.IO) {
            personalAnimeDAO.mergeAnimePersonalStatus(status)
        }
        return true
    }

    override suspend fun getAnimeMainPicture(pictureId: Long): PicturePersistence? {
        return withContext(dispatchers.IO) {
            try {
                return@withContext sideDAO.getAnimeMainPictureById(pictureId = pictureId)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                return@withContext null
            }

        }
    }

    override suspend fun getPersonalAnimeListSyncJobs(): List<DeferredPersonalAnimeListChange> {
        return withContext(dispatchers.IO) {
            syncJobDao.getPersonalAnimeListSyncJobs()
        }
    }

    override suspend fun removePersonalAnimeListSyncJob(deferredJob: List<DeferredPersonalAnimeListChange>): Boolean {
        return withContext(dispatchers.IO) {
            syncJobDao.deletePersonalAnimeListSyncJob(deferredJob)
            true
        }
    }

    override suspend fun removePersonalAnimeListSyncJob(animeId: Int): Boolean {
        return withContext(dispatchers.IO) {
            syncJobDao.deletePersonalAnimeListSyncJob(animeId)
            true
        }
    }

    override suspend fun getAnimePersonalStatus(animeId: Int): AnimePersonalStatusPersistence? {
        return withContext(dispatchers.IO) {
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

    override suspend fun updateUserCache(user: User) = withContext(dispatchers.IO) {
        userDAO.clearUserData()
        userDAO.addUserData(
            UserPersistence(
                id = user.id,
                name = user.name,
                gender = user.gender,
                birthday = user.birthday?.let { UserMapper.bdayFormat.format(it) },
                location = user.location,
                joinedAt = user.joinedAt?.let { UserMapper.format.format(it) },
                itemsWatching = user.userAnimeStatistic?.itemsWatching,
                itemsCompleted = user.userAnimeStatistic?.itemsCompleted,
                itemsOnHold = user.userAnimeStatistic?.itemsOnHold,
                itemsDropped = user.userAnimeStatistic?.itemsDropped,
                itemsPlanToWatch = user.userAnimeStatistic?.itemsPlanToWatch,
                items = user.userAnimeStatistic?.items,
                daysWatched = user.userAnimeStatistic?.daysWatched,
                daysWatching = user.userAnimeStatistic?.daysWatching,
                daysCompleted = user.userAnimeStatistic?.daysCompleted,
                daysOnHold = user.userAnimeStatistic?.daysOnHold,
                daysDropped = user.userAnimeStatistic?.daysDropped,
                days = user.userAnimeStatistic?.days,
                episodes = user.userAnimeStatistic?.episodes,
                meanScore = user.userAnimeStatistic?.meanScore,
                picture = user.picture,
            )
        )
    }

    override suspend fun deleteAnimePersonalStatusFromCache(animeId: Int): Boolean {
        return withContext(dispatchers.IO) {
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
        return withContext(dispatchers.IO) {
            try {
                val startSeasonId = anime.startSeason?.let { startSeason ->
                    return@let sideDAO.getSeasonByYearAndSeason(startSeason.year,
                        startSeason.season)?.id
                        ?: sideDAO.addSeason(
                            SeasonPersistence(
                                0,
                                anime.startSeason.year,
                                anime.startSeason.season,
                            )
                        )
                }

                val mainPictureId = anime.picture?.let { picture ->
                    return@let sideDAO.addPicture(
                        PicturePersistence(
                            sideDAO.getAnimeMainPictureByAnimeId(anime.id)?.id ?: 0,
                            null,
                            picture.large,
                            picture.medium
                        )
                    )
                }

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
                    )
                )

                if (anime.relatedAnime.isNotEmpty()) {
                    saveRelatedAnime(anime, anime.relatedAnime)
                }

                if (anime.recommendedAnime.isNotEmpty()) {
                    saveRecommendedAnime(anime, anime.recommendedAnime)
                }

                if (anime.pictures.isNotEmpty()) {
                    saveAnimePictures(anime, anime.pictures)
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
        withContext(dispatchers.IO) {
            for (genre in genres) {
                sideDAO.addGenre(GenrePersistence(
                    genre.id,
                    genre.name)
                )
                animeDAO.addAnimeGenre(
                    AnimeGenrePersistence(
                        anime.id, genre.id
                    )
                )
            }
        }

    suspend fun saveAnimePictures(anime: Anime, pictures: List<Picture>) =
        withContext(dispatchers.IO) {
            sideDAO.replaceAnimePictures(
                anime.id, pictures.map {
                    PicturePersistence(
                        0,
                        anime.id,
                        it.large,
                        it.medium,
                    )
                }
            )
        }

    suspend fun saveRelatedAnime(anime: Anime, relatedAnimeList: List<RelatedAnime>) =
        withContext(dispatchers.IO) {
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
        withContext(dispatchers.IO) {
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

    override suspend fun getUserCache(): UserPersistence? = withContext(dispatchers.IO) {
        try {
            userDAO.getUserData()
        } catch (e: Exception) {
            null
        }
    }
}
