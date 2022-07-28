package com.kis.youranimelist.model.api

import com.fasterxml.jackson.annotation.JsonProperty

data class GenreResponse(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String,
)
