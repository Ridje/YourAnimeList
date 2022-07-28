package com.kis.youranimelist.model.api.ranking_response

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kis.youranimelist.model.api.StartSeason

@JsonPropertyOrder("node", "ranking")
data class AnimeRanked(
    @JsonProperty("node") var anime: AnimeRankingItem,
    @JsonProperty("ranking") var ranking: Ranking,
)
