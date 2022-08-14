package com.kis.youranimelist.data.repository

import androidx.room.Transaction
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO
import com.kis.youranimelist.data.cache.dao.UserDAO
import com.kis.youranimelist.data.cache.model.AnimePersistence
import com.kis.youranimelist.data.cache.model.AnimePersonalStatusEntity
import com.kis.youranimelist.data.cache.model.AnimeStatusPersistence
import com.kis.youranimelist.data.cache.model.AnimeWithPersonalStatusPersistence
import com.kis.youranimelist.data.cache.model.UserPersistence
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.user.mapper.UserMapper
import com.kis.youranimelist.domain.user.model.User
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalDataSourceImpl(
    private val personalAnimeDAO: PersonalAnimeDAO,
    private val animeDAO: AnimeDAO,
    private val userDAO: UserDAO,
    private val dispatchers: Dispatchers,
) : LocalDataSource {

    @Transaction
    override suspend fun saveAnimeWithPersonalStatusToCache(status: AnimeStatus) =
        withContext(dispatchers.IO) {
            try {
                val animeCache = AnimePersistence(
                    id = status.anime.id,
                    name = status.anime.title,
                    numEpisodes = status.anime.numEpisodes ?: 0,
                    mean = status.anime.mean ?: 0.0f,
                    mediaType = status.anime.mediaType ?: "unknown",
                    pictureLink = status.anime.picture?.large ?: "",
                )
                val statusCache = AnimeStatusPersistence(status.status.presentIndex)

                val personalAnimeStatus = AnimePersonalStatusEntity(
                    score = status.score,
                    episodesWatched = status.numWatchedEpisodes,
                    statusId = status.status.presentIndex,
                    animeId = status.anime.id,
                )

                animeDAO.addAnime(animeCache)
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
                val animeCache = AnimePersistence(
                    id = status.anime.id,
                    name = status.anime.title,
                    numEpisodes = status.anime.numEpisodes ?: 0,
                    mean = status.anime.mean ?: 0.0f,
                    mediaType = status.anime.mediaType ?: "unknown",
                    pictureLink = status.anime.picture?.large ?: "",
                )
                val statusCache = AnimeStatusPersistence(status.status.presentIndex)

                val personalAnimeStatus = AnimePersonalStatusEntity(
                    score = status.score,
                    episodesWatched = status.numWatchedEpisodes,
                    statusId = status.status.presentIndex,
                    animeId = status.anime.id,
                )

                animeDAO.addAnime(animeCache)
                personalAnimeDAO.addAnimeStatus(statusCache)
                personalAnimeDAO.addPersonalAnimeStatus(personalAnimeStatus)
            }
            return@withContext true
        }

    override suspend fun savePersonalAnimeStatusToCache(status: AnimePersonalStatusEntity): Boolean {
        return withContext(dispatchers.IO) {
            personalAnimeDAO.addPersonalAnimeStatus(status)
            return@withContext true
        }
    }

    override fun getAnimeWithStatusProducerFromCache(): Flow<List<AnimeWithPersonalStatusPersistence>> {
        return personalAnimeDAO.getAllAnimeWithPersonalStatuses()
    }

    override fun getAnimeWithStatusProducerFromCache(id: Int): Flow<AnimeWithPersonalStatusPersistence?> {
        return personalAnimeDAO.getAnimeWithPersonalStatus(id)
    }

    @Transaction
    override suspend fun updateUserCache(user: User) {
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
            personalAnimeDAO.deletePersonalAnimeStatus(animeId)
            return@withContext true
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
