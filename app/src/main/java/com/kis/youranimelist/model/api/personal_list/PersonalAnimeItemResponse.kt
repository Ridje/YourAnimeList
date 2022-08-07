package com.kis.youranimelist.model.api.personal_list

import com.kis.youranimelist.model.api.AnimeResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalAnimeItemResponse(
    @SerialName("node") val anime: AnimeResponse,
    @SerialName("list_status") val status: AnimeStatusResponse,
)
