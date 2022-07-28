package com.kis.youranimelist.model.api.ranking_response

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kis.youranimelist.model.api.PictureResponse
import com.kis.youranimelist.model.api.StartSeason
import kotlinx.parcelize.Parcelize

@JsonPropertyOrder(
    "id",
    "title",
    "main_picture"
)

@Parcelize
data class AnimeRankingItem(
    @JsonProperty("id") var id: Int,
    @JsonProperty("title") var title: String,
    @JsonProperty("main_picture") var pictureResponse: PictureResponse?,
    @JsonProperty("nsfw") var nsfw: String?,
    @JsonProperty("start_season") var startSeason: StartSeason?,
) : Parcelable
