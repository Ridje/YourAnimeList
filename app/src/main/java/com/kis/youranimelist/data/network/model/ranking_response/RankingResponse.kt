package com.kis.youranimelist.data.network.model.ranking_response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankingResponse(
    @SerialName("rank") var rank: Int,
    @SerialName("previous_rank") var previousRank: Int?,
)
