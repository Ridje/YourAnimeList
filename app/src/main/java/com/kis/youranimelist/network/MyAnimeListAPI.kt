package com.kis.youranimelist.network

import com.kis.youranimelist.model.api.AnimeResponse
import com.kis.youranimelist.model.api.UserResponse
import com.kis.youranimelist.model.api.personal_list.PersonalAnimeListResponse
import com.kis.youranimelist.model.api.ranking_response.Root
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MyAnimeListAPI {

    @GET("anime/ranking")
    fun animeRanking(
        @Query("ranking_type") rankingType : String,
        @Query("limit") limit : Int?,
        @Query("offset") offset : Int?,
        @Query("fields") fields : String?) : Call<Root>

    @GET("anime/{anime_id}")
    fun animeDetails(
        @Path("anime_id") animeID : Int,
        @Query("fields") fields : String?
    ) : Call<AnimeResponse>

    @GET("users/@me")
    fun userProfile(
        @Query("fields") fields: String?,
    ) : Call<UserResponse>

    @GET("users/@me/animelist")
    fun userAnime(
        @Query("status") status: String?,
        @Query("sort") sort: String?,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("fields") fields: String?,
    ): Call<PersonalAnimeListResponse>
}
