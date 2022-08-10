package com.kis.youranimelist.data.network.model.personal_list

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeStatusResponse(
    @SerialName("status") val status: String,
    @SerialName("score") val score: Int,
    @SerialName("num_episodes_watched") val numEpisodesWatched: Int,
)
