package com.kis.youranimelist.data.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.network.model.AnimeResponse
import com.kis.youranimelist.data.network.model.ErrorResponse
import com.kis.youranimelist.data.network.model.UserResponse
import com.kis.youranimelist.data.network.model.personal_list.AnimeStatusResponse
import com.kis.youranimelist.data.network.model.personal_list.PersonalAnimeListResponse
import com.kis.youranimelist.data.network.model.ranking_response.RankingRootResponse
import com.kis.youranimelist.data.network.model.searchresponse.SearchingRootResponse
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface MyAnimeListAPI {

    @GET("anime/ranking")
    suspend fun animeRanking(
        @Query("ranking_type") rankingType: String,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?,
        @Query("fields") fields: String?,
    ): NetworkResponse<RankingRootResponse, ErrorResponse>

    @GET("anime")
    suspend fun animeSearching(
        @Query("q") search: String,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?,
        @Query("fields") fields: String?,
    ): NetworkResponse<SearchingRootResponse, ErrorResponse>

    @GET("anime/{anime_id}")
    fun animeDetails(
        @Path("anime_id") animeID: Int,
        @Query("fields") fields: String,
    ): Call<AnimeResponse>

    @GET("users/@me")
    suspend fun userProfile(
        @Query("fields") fields: String?,
    ): NetworkResponse<UserResponse, ErrorResponse>

    @GET("users/@me/animelist")
    fun userAnime(
        @Query("status") status: String?,
        @Query("sort") sort: String?,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("fields") fields: String?,
    ): Call<PersonalAnimeListResponse>

    @PATCH("anime/{anime_id}/my_list_status")
    @FormUrlEncoded
    fun updateUserAnime(
        @Path("anime_id") animeID: Int,
        @Field("status") status: String?,
        @Field("score") score: Int?,
        @Field("num_watched_episodes") episodesWatched: Int?,
    ): Call<AnimeStatusResponse>

    @PATCH("anime/{anime_id}/my_list_status")
    fun getUserAnimeStatus(
        @Path("anime_id") animeId: Int,
    ): Call<AnimeStatusResponse>

    @DELETE("anime/{anime_id}/my_list_status")
    fun deleteUserAnimeStatus(
        @Path("anime_id") animeId: Int,
    ): Call<Unit>
}
