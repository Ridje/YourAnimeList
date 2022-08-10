package com.kis.youranimelist.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("picture") val picture: String?,
    @SerialName("gender") val gender: String?,
    @SerialName("birthday") val birthday: String?,
    @SerialName("location") val location: String?,
    @SerialName("joined_at") val joinedAt: String?,
    @SerialName("anime_statistics") val animeStatistics: AnimeStatisticsResponse?,
)
