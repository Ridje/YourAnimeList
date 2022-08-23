package com.kis.youranimelist.data.repository

import com.kis.youranimelist.data.network.model.AnimeResponse
import com.kis.youranimelist.data.network.model.TokenResponse
import com.kis.youranimelist.data.network.model.UserResponse
import com.kis.youranimelist.data.network.model.personal_list.AnimeStatusResponse
import com.kis.youranimelist.data.network.model.personal_list.PersonalAnimeListResponse
import com.kis.youranimelist.data.network.model.ranking_response.AnimeRankedResponse

interface RemoteDataSource {

    companion object {
        const val CODE_FIELD = "code"
    }

    fun getAccessToken(clientID : String, code: String, codeVerifier: String, grantType : String) : TokenResponse
    fun getUserData(): UserResponse?

    suspend fun getAnimeInfo(animeID : Int) : AnimeResponse?
    suspend fun getAnimeRankingList(rankingType : String, limit : Int?, offset : Int?,) : List<AnimeRankedResponse>
    suspend fun getPersonalAnimeList(status: String?, sort: String?, limit: Int, offset: Int): PersonalAnimeListResponse?
    suspend fun deletePersonalAnimeStatus(animeId: Int): Boolean
    suspend fun savePersonalAnimeStatus(animeId: Int, status: String?, score: Int?, episodesWatched: Int?): Boolean
    suspend fun getPersonalAnimeStatus(animeId: Int): AnimeStatusResponse?
}
