package com.kis.youranimelist.network

import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.model.ranking_response.AnimeRanked
import com.kis.youranimelist.model.ranking_response.Root
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
    ) : Call<Anime>
}