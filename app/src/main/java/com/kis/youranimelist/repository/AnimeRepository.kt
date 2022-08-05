package com.kis.youranimelist.repository

import com.kis.youranimelist.model.api.Token
import com.kis.youranimelist.model.app.Anime
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
    ): Token

    fun getFavouriteAnime(): Anime?
}
