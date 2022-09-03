package com.kis.youranimelist.data.network.model.suggestingresponse

import com.kis.youranimelist.data.network.model.AnimeResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SuggestedAnimeResponse(
    @SerialName("node") var anime: AnimeResponse,
)
