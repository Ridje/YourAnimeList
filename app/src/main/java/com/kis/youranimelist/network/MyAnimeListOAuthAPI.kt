package com.kis.youranimelist.network

import com.kis.youranimelist.model.api.Token
import retrofit2.Call
import retrofit2.http.*

interface MyAnimeListOAuthAPI {

    @FormUrlEncoded
    @POST("token")
    fun getAccessToken(
        @Field("client_id") clientID : String,
        @Field("code") code : String,
        @Field("code_verifier") codeVerifier : String,
        @Field("grant_type") grantType : String) : Call<Token>
}