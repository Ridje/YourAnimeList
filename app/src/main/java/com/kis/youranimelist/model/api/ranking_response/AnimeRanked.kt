package com.kis.youranimelist.model.api.ranking_response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeRanked(
    @SerialName("node") var anime: AnimeRankingItem,
    @SerialName("ranking") var ranking: Ranking,
)
