package com.kis.youranimelist.repository

import android.accounts.NetworkErrorException
import com.fasterxml.jackson.databind.ObjectMapper
import com.kis.youranimelist.BuildConfig
import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.model.Token
import com.kis.youranimelist.model.ranking_response.AnimeRanked
import com.kis.youranimelist.utils.Urls
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

interface Repository {

    companion object {
        const val GRANT_TYPE = "authorization_code"
        const val CLIENT_ID_FIELD = "client_id"
        const val CODE_VERIFIER_FIELD = "code_verifier"
        const val GRANT_TYPE_FIELD = "grant_type"
        const val CODE_FIELD = "code"
    }

    fun getAnimeRankingList(rankingType : String, limit : Int?, offset : Int?, fields : String?) : List<AnimeRanked>
    fun getAnimeInfo(animeID : Int, keys : String?) : Anime
    fun getAccessToken(clientID : String, code: String, codeVerifier: String, grantType : String) : Token
}
