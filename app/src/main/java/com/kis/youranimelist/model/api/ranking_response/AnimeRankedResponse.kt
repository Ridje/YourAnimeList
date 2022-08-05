package com.kis.youranimelist.model.api.ranking_response

import com.kis.youranimelist.model.api.AnimeResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeRankedResponse(
    @SerialName("node") var anime: AnimeResponse,
    @SerialName("ranking") var ranking: RankingResponse,
)
