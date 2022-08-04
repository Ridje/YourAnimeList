package com.kis.youranimelist.repository

import com.kis.youranimelist.model.api.AnimeResponse
import com.kis.youranimelist.model.api.Token
import com.kis.youranimelist.model.api.UserResponse
import com.kis.youranimelist.model.api.ranking_response.AnimeRanked
import com.kis.youranimelist.model.app.User

interface RemoteDataSource {

    companion object {
        const val CODE_FIELD = "code"
    }

    fun getAnimeRankingList(rankingType : String, limit : Int?, offset : Int?, fields : String?) : List<AnimeRanked>
    fun getAnimeInfo(animeID : Int, keys : String?) : AnimeResponse
    fun getAccessToken(clientID : String, code: String, codeVerifier: String, grantType : String) : Token
    fun getUserData(): UserResponse
}
