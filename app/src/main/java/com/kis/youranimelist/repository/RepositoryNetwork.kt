package com.kis.youranimelist.repository

import android.accounts.NetworkErrorException
import android.os.Build
import com.fasterxml.jackson.databind.ObjectMapper
import com.kis.youranimelist.BuildConfig
import com.kis.youranimelist.YourAnimeListApplication
import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.model.StartSeason
import com.kis.youranimelist.model.Token
import com.kis.youranimelist.model.ranking_response.AnimeRanked
import com.kis.youranimelist.network.CustomOkHTTPClient
import com.kis.youranimelist.network.MyAnimeListAPI
import com.kis.youranimelist.network.MyAnimeListOAuthAPI
import com.kis.youranimelist.utils.Urls
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object RepositoryNetwork : Repository {

    private var malService = Retrofit
        .Builder()
        .baseUrl(Urls.apiBaseUrl)
        .addConverterFactory(JacksonConverterFactory.create())
        .client(CustomOkHTTPClient.getOkHTTPClient(YourAnimeListApplication.accessTokenType, YourAnimeListApplication.accessToken))
        .build()
        .create(MyAnimeListAPI::class.java)

    private var malOAuthService = Retrofit
        .Builder()
        .baseUrl(Urls.oauthBaseUrl)
        .addConverterFactory(JacksonConverterFactory.create())
        .client(CustomOkHTTPClient.getOkHTTPClient(YourAnimeListApplication.accessTokenType, YourAnimeListApplication.accessToken))
        .build()
        .create(MyAnimeListOAuthAPI::class.java)

    fun rebuildServices() {
        malService = Retrofit
            .Builder()
            .baseUrl(Urls.apiBaseUrl)
            .addConverterFactory(JacksonConverterFactory.create())
            .client(CustomOkHTTPClient.getOkHTTPClient(YourAnimeListApplication.accessTokenType, YourAnimeListApplication.accessToken))
            .build()
            .create(MyAnimeListAPI::class.java)

        malOAuthService = Retrofit
            .Builder()
            .baseUrl(Urls.oauthBaseUrl)
            .addConverterFactory(JacksonConverterFactory.create())
            .client(CustomOkHTTPClient.getOkHTTPClient(YourAnimeListApplication.accessTokenType, YourAnimeListApplication.accessToken))
            .build()
            .create(MyAnimeListOAuthAPI::class.java)
    }

    override fun getAnimeRankingList(rankingType : String, limit : Int?, offset : Int?, fields : String?): List<AnimeRanked> {

        val result = malService.animeRanking(rankingType, limit, offset, fields).execute()

        return result.body()?.data ?: ArrayList()
    }

    override fun getAnimeInfo(animeID : Int, keys : String?): Anime {

        val result = malService.animeDetails(animeID, keys).execute()

        return when {
            result.isSuccessful -> result.body() ?: Anime(1, "Promise", null, StartSeason(2021, "spring"), null)
            else -> throw NetworkErrorException()
        }
    }

    override fun getAccessToken(clientID : String, code: String, codeVerifier: String, grantType : String): Token {

        val result = malOAuthService.getAccessToken(clientID, code, codeVerifier, grantType).execute()

        return when {
            result.isSuccessful -> result.body() ?: throw NetworkErrorException()
            else -> throw NetworkErrorException()
        }

    }
}