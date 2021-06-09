package com.kis.youranimelist.repository

import android.accounts.NetworkErrorException
import com.kis.youranimelist.model.api.Anime
import com.kis.youranimelist.model.api.StartSeason
import com.kis.youranimelist.model.api.Token
import com.kis.youranimelist.model.api.ranking_response.AnimeRanked
import com.kis.youranimelist.network.MyAnimeListAPI
import com.kis.youranimelist.network.MyAnimeListOAuthAPI


class RepositoryNetwork(val malService : MyAnimeListAPI,
                        val malOauthService : MyAnimeListOAuthAPI) : Repository {


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

        val result = malOauthService.getAccessToken(clientID, code, codeVerifier, grantType).execute()

        return when {
            result.isSuccessful -> result.body() ?: throw NetworkErrorException()
            else -> throw NetworkErrorException()
        }

    }
}