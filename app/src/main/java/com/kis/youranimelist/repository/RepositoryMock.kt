package com.kis.youranimelist.repository

import com.kis.youranimelist.model.api.Anime
import com.kis.youranimelist.model.api.StartSeason
import com.kis.youranimelist.model.api.Token
import com.kis.youranimelist.model.api.ranking_response.AnimeRankingItem
import com.kis.youranimelist.model.api.ranking_response.AnimeRanked
import com.kis.youranimelist.model.api.ranking_response.Ranking

object RepositoryMock : Repository {


    override fun getAnimeRankingList(rankingType : String, limit : Int?, offset : Int?, fields : String?): List<AnimeRanked> {

        return listOf(
            AnimeRanked(
                AnimeRankingItem(
                    42361,
                    "Ijiranaide, Nagatoro-san",
                    null
                ),
                Ranking(1, 10)
            )
        )
    }

    override fun getAnimeInfo(animeID : Int, keys : String?): Anime {
        return Anime(1, "Promise", null, StartSeason(2021, "spring"), null)
    }

    override fun getAccessToken(clientID : String, code: String, codeVerifier: String, grantType : String): Token {
        return Token("1", "2", 3, "4")
    }

}