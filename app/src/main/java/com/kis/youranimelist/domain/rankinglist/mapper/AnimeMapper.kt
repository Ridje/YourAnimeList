package com.kis.youranimelist.domain.rankinglist.mapper

import com.kis.youranimelist.data.network.model.ranking_response.AnimeRankedResponse
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.rankinglist.model.Picture
import com.kis.youranimelist.data.cache.model.AnimePersistence
import javax.inject.Inject

class AnimeMapper @Inject constructor() {
    fun map(from: AnimeRankedResponse): Anime {
        return Anime(from.anime, from.ranking)
    }

    fun map(from: AnimePersistence): Anime {
        return Anime(
            id = from.id,
            title = from.name,
            numEpisodes = from.numEpisodes,
            picture = Picture(large = from.pictureLink, null),
            mean = from.mean,
            mediaType = from.mediaType,
        )
    }
}
