package com.kis.youranimelist.model.api.ranking_response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Root(
    @SerialName("data") var data: List<AnimeRankedResponse>,
    @SerialName("paging") var paging: Paging,
)
