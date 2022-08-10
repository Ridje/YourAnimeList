package com.kis.youranimelist.data.repository

import com.kis.youranimelist.data.network.model.TokenResponse
import com.kis.youranimelist.domain.rankinglist.model.Anime
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    val animeCache: Map<Int, Anime>
    suspend fun getRankingAnimeList(
        rankingType: String,
        limit: Int?,
        offset: Int?,
        fields: String?,
    ): List<Anime>

    fun getAnimeDetailedData(animeID: Int, keys: String?): Flow<Anime?>
    fun getAccessToken(
        clientID: String,
        code: String,
        codeVerifier: String,
        grantType: String,
    ): TokenResponse

    fun getFavouriteAnime(): Anime?
}
