package com.kis.youranimelist.data.network.model

import com.kis.youranimelist.core.utils.AppPreferences
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName(AppPreferences.ACCESS_TOKEN_SETTING_KEY) var accessToken: String,
    @SerialName(AppPreferences.REFRESH_TOKEN_SETTING_KEY) var refreshToken: String,
    @SerialName(AppPreferences.EXPIRES_IN_TOKEN_SETTING_KEY) var expiresIn: Int,
    @SerialName(AppPreferences.TYPE_TOKEN_SETTING_KEY) var tokenType: String,
)
