package com.kis.youranimelist.data.repository

import androidx.room.Transaction
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.data.cache.model.AnimePersistence
import com.kis.youranimelist.data.cache.model.AnimePersonalStatusEntity
import com.kis.youranimelist.data.cache.model.AnimeStatusPersistence
import com.kis.youranimelist.data.cache.model.AnimeWithPersonalStatusPersistence
import com.kis.youranimelist.data.cache.dao.AnimeDAO
import com.kis.youranimelist.data.cache.dao.PersonalAnimeDAO

class LocalDataSourceImpl(
    private val personalAnimeDAO: PersonalAnimeDAO,
    private val animeDAO: AnimeDAO,
) : LocalDataSource {

    @Transaction
    override suspend fun savePersonalAnimeStatusToCache(status: AnimeStatus) {

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

    @Transaction
    override suspend fun savePersonalAnimeStatusToCache(statuses: List<AnimeStatus>) {
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
    }

    override suspend fun getPersonalAnimeStatusFromCache(): List<AnimeWithPersonalStatusPersistence> {
        return personalAnimeDAO.getAllAnimeWithPersonalStatuses()
    }
}
