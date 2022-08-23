package com.kis.youranimelist.data.repository.anime

import com.kis.youranimelist.data.network.model.TokenResponse
import com.kis.youranimelist.domain.rankinglist.model.Anime
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {

    suspend fun refreshAnimeDetailedData(animeID: Int): Boolean


    suspend fun getAccessToken(
        clientID: String,
        code: String,
        codeVerifier: String,
        grantType: String,
    ): TokenResponse

    suspend fun getRankingAnimeList(rankingType: String, limit: Int?, offset: Int?): List<Anime>
    fun getAnimeDetailedDataSource(animeID: Int): Flow<Anime>
}
