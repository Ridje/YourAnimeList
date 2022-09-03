package com.kis.youranimelist.data.network.model.suggestingresponse

import com.kis.youranimelist.data.network.model.PagingResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuggestingRootResponse(
    @SerialName("data") val data: List<SuggestedAnimeResponse>,
    @SerialName("paging") val paging: PagingResponse,
)
