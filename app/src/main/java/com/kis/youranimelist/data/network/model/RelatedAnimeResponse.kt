package com.kis.youranimelist.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelatedAnimeResponse(
    @SerialName("node") val node: AnimeResponse,
    @SerialName("relation_type") val relationType: String,
    @SerialName("relation_type_formatted") val relationTypeFormatted: String,
)
