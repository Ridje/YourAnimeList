package com.kis.youranimelist.data.network.model.personallist

import com.kis.youranimelist.data.network.model.PagingResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalAnimeListResponse(
    @SerialName("data") val data: List<PersonalAnimeItemResponse>,
    @SerialName("paging") val paging: PagingResponse,
)
