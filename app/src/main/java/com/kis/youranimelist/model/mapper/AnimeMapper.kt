package com.kis.youranimelist.model.mapper

import com.kis.youranimelist.model.api.ranking_response.AnimeRankedResponse
import com.kis.youranimelist.model.app.Anime
import javax.inject.Inject

class AnimeMapper @Inject constructor() {
    fun map(from: AnimeRankedResponse): Anime {
        return Anime(from.anime, from.ranking)
    }
}
