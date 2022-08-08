package com.kis.youranimelist.repository

import com.kis.youranimelist.model.api.AnimeResponse
import com.kis.youranimelist.model.api.Token
import com.kis.youranimelist.model.api.UserResponse
import com.kis.youranimelist.model.api.personal_list.PersonalAnimeListResponse
import com.kis.youranimelist.model.api.ranking_response.AnimeRankedResponse

interface RemoteDataSource {

    companion object {
        const val CODE_FIELD = "code"
    }

    suspend fun getAnimeRankingList(rankingType : String, limit : Int?, offset : Int?, fields : String?) : List<AnimeRankedResponse>
    fun getAnimeInfo(animeID : Int, keys : String?) : AnimeResponse
    fun getAccessToken(clientID : String, code: String, codeVerifier: String, grantType : String) : Token
    fun getUserData(): UserResponse
    suspend fun getPersonalAnimeList(status: String?, sort: String?, limit: Int, offset: Int): PersonalAnimeListResponse
}
