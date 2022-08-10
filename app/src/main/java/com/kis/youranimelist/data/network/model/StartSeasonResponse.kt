package com.kis.youranimelist.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartSeasonResponse(
    @SerialName("year") var year: Int,
    @SerialName("season") var season: String,
)
