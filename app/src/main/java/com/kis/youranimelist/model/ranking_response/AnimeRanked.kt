package com.kis.youranimelist.model.ranking_response

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("node", "ranking")
data class AnimeRanked(
    @JsonProperty("node") var anime: AnimeRankingItem,
    @JsonProperty("ranking") var ranking: Ranking
)