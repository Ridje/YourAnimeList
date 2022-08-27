package com.kis.youranimelist.data.network.model.searchresponse

import com.kis.youranimelist.data.network.model.AnimeResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeSearchResponse(
    @SerialName("node") var anime: AnimeResponse,
)
