package com.kis.youranimelist.model.api

import com.fasterxml.jackson.annotation.JsonProperty
import com.kis.youranimelist.utils.AppPreferences

data class Token(
    @JsonProperty(AppPreferences.ACCESS_TOKEN_SETTING_KEY) var accessToken : String,
    @JsonProperty(AppPreferences.REFRESH_TOKEN_SETTING_KEY) var refreshToken : String,
    @JsonProperty(AppPreferences.EXPIRES_IN_TOKEN_SETTING_KEY) var expiresIn : Int,
    @JsonProperty(AppPreferences.TYPE_TOKEN_SETTING_KEY) var tokenType : String)
