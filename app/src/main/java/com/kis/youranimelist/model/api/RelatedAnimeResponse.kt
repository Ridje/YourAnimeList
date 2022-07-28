package com.kis.youranimelist.model.api

import com.fasterxml.jackson.annotation.JsonProperty

data class RelatedAnimeResponse(
    @JsonProperty("node") val node: AnimeResponse,
    @JsonProperty("relation_type") val relationType: String,
    @JsonProperty("relation_type_formatted") val relationTypeFormatted: String,
)
