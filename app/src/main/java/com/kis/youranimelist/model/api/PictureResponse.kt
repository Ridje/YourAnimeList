package com.kis.youranimelist.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PictureResponse(
    @SerialName("large") var large: String?,
    @SerialName("medium") var medium: String,
)
