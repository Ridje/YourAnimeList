package com.kis.youranimelist.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartSeasonResponse(
    @SerialName("year") var year: Int,
    @SerialName("season") var season: String,
)
