package com.kis.youranimelist.data.network.model.personallist

import com.kis.youranimelist.data.network.model.AnimeResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalAnimeItemResponse(
    @SerialName("node") val anime: AnimeResponse,
    @SerialName("list_status") val status: AnimeStatusResponse,
)
