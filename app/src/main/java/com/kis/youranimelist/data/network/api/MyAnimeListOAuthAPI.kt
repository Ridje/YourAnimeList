package com.kis.youranimelist.data.network.api

import com.kis.youranimelist.data.network.model.TokenResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MyAnimeListOAuthAPI {

    @FormUrlEncoded
    @POST("token")
    fun getAccessToken(
        @Field("client_id") clientID: String,
        @Field("code") code: String,
        @Field("code_verifier") codeVerifier: String,
        @Field("grant_type") grantType: String,
    ): Call<TokenResponse>

    @FormUrlEncoded
    @POST("token")
    fun refreshToken(
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String,
    ): Call<TokenResponse>
}
