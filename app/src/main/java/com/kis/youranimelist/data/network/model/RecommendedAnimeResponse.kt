package com.kis.youranimelist.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendedAnimeResponse(
    @SerialName("node") val node: AnimeResponse,
    @SerialName("num_recommendations") val recommendedTimes: Int,
)
