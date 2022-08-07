package com.kis.youranimelist.model.api.personal_list

import com.kis.youranimelist.model.api.PagingResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalAnimeListResponse(
    @SerialName("data") val data: List<PersonalAnimeItemResponse>,
    @SerialName("paging") val paging: PagingResponse,
)
