package com.kis.youranimelist.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeResponse(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("main_picture") val pictureResponse: PictureResponse?,
    @SerialName("start_season") val startSeasonResponse: StartSeasonResponse?,
    @SerialName("mean") val mean: Float?,
    @SerialName("synopsis") val synopsis: String? = "Description",
    @SerialName("genres") val genres: List<GenreResponse>?,
    @SerialName("pictures") val pictures: List<PictureResponse>?,
    @SerialName("related_anime") val relatedAnime: List<RelatedAnimeResponse>?,
)
