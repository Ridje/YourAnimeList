package com.kis.youranimelist.domain.personalanimelist.mapper

import com.kis.youranimelist.data.cache.model.personalanime.PersonalStatusOfAnimePersistence
import com.kis.youranimelist.data.network.model.personal_list.PersonalAnimeItemResponse
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatusValue
import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.domain.rankinglist.model.Anime
import javax.inject.Inject

class AnimeStatusMapper @Inject constructor(
    private val animeMapper: AnimeMapper,
) {
    fun map(from: PersonalAnimeItemResponse): AnimeStatus {
        val anime = Anime(from.anime)
        val status = AnimeStatusValue.Companion.Factory.getAnimeStatusByValue(from.status.status)
        return AnimeStatus(
            anime = anime,
            status = status,
            score = from.status.score,
            numWatchedEpisodes = from.status.numEpisodesWatched,
        )
    }

    fun map(from: PersonalStatusOfAnimePersistence): AnimeStatus {
        val anime = animeMapper.map(from.anime, from.mainPicture)
        val status =
            AnimeStatusValue.Companion.Factory.getAnimeStatusByValue(from.animeStatusPersistence.id)
        return AnimeStatus(
            anime = anime,
            status = status,
            score = from.status.score,
            numWatchedEpisodes = from.status.episodesWatched,
        )
    }
}
