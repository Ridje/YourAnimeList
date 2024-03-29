package com.kis.youranimelist.data.network.model.rankingresponse

import com.kis.youranimelist.data.network.model.PagingResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankingRootResponse(
    @SerialName("data") val data: List<AnimeRankedResponse>,
    @SerialName("paging") val paging: PagingResponse,
)
