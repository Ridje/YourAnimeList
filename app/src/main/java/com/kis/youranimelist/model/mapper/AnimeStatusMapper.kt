package com.kis.youranimelist.model.mapper

import com.kis.youranimelist.model.api.personal_list.PersonalAnimeItemResponse
import com.kis.youranimelist.model.app.Anime
import com.kis.youranimelist.model.app.AnimeStatus
import com.kis.youranimelist.model.app.AnimeStatusValue
import javax.inject.Inject

class AnimeStatusMapper @Inject constructor(
    private val animeMapper: AnimeMapper,
) {
    fun map(from: PersonalAnimeItemResponse): AnimeStatus {
        val anime = Anime(from.anime)
        val status = AnimeStatusValue.Fabric.getAnimeStatusByValue(from.status.status)
        return AnimeStatus(
            anime = anime,
            status = status,
            score = from.status.score,
            numWatchedEpisodes = from.status.numEpisodesWatched,
        )
    }
}
