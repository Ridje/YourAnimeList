package com.kis.youranimelist.data.network.model.rankingresponse

import com.kis.youranimelist.data.network.model.AnimeResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeRankedResponse(
    @SerialName("node") var anime: AnimeResponse,
    @SerialName("ranking") var ranking: RankingResponse,
)
