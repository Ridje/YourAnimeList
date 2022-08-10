package com.kis.youranimelist.data.network.model.ranking_response

import com.kis.youranimelist.data.network.model.PictureResponse
import com.kis.youranimelist.data.network.model.StartSeasonResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeRankingItem(
    @SerialName("id") var id: Int,
    @SerialName("title") var title: String,
    @SerialName("main_picture") var pictureResponse: PictureResponse?,
    @SerialName("nsfw") var nsfw: String?,
    @SerialName("start_season") var startSeasonResponse: StartSeasonResponse?,
)
