package com.kis.youranimelist.model.api

import com.fasterxml.jackson.annotation.JsonProperty

data class AnimeResponse(
    @JsonProperty("id") val id: Int,
    @JsonProperty("title") val title: String,
    @JsonProperty("main_picture") val pictureResponse: PictureResponse?,
    @JsonProperty("start_season") val startSeason: StartSeason?,
    @JsonProperty("mean") val mean: Float?,
    @JsonProperty("synopsis") val synopsis: String? = "Description",
    @JsonProperty("genres") val genres: List<GenreResponse>?,
    @JsonProperty("pictures") val pictures: List<PictureResponse>?,
    @JsonProperty("related_anime") val relatedAnime: List<RelatedAnimeResponse>?,
)
