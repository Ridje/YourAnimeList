package com.kis.youranimelist.repository

import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.model.Token
import com.kis.youranimelist.model.ranking_response.AnimeRanked

interface Repository {

    companion object {
        var repositoryImpl : Repository = RepositoryNetwork
        const val GRANT_TYPE = "authorization_code"
        const val CLIENT_ID_FIELD = "client_id"
        const val CODE_VERIFIER_FIELD = "code_verifier"
        const val GRANT_TYPE_FIELD = "grant_type"
        const val CODE_FIELD = "code"
        fun setRepository(airplaneMode : Boolean) {
            if (airplaneMode) {
                repositoryImpl = RepositoryMock
            }
        }
    }

    fun getAnimeRankingList(rankingType : String, limit : Int?, offset : Int?, fields : String?) : List<AnimeRanked>
    fun getAnimeInfo(animeID : Int, keys : String?) : Anime
    fun getAccessToken(clientID : String, code: String, codeVerifier: String, grantType : String) : Token
}
