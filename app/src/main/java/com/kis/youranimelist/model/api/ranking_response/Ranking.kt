package com.kis.youranimelist.model.api.ranking_response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Ranking(
    @SerialName("rank") var rank: Int,
    @SerialName("previous_rank") var previousRank: Int?,
)
