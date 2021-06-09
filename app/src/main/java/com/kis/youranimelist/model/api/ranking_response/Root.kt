package com.kis.youranimelist.model.api.ranking_response

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(
    "data",
    "paging")
data class Root(
    @JsonProperty("data") var data: List<AnimeRanked>,
    @JsonProperty("paging") var paging: Paging
)
