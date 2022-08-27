package com.kis.youranimelist.data.network.model.searchresponse

import com.kis.youranimelist.data.network.model.PagingResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchingRootResponse(
    @SerialName("data") val data: List<AnimeSearchResponse>,
    @SerialName("paging") val paging: PagingResponse,
)
