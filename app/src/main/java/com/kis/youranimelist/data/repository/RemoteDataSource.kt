package com.kis.youranimelist.data.repository

import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.network.model.AnimeResponse
import com.kis.youranimelist.data.network.model.ErrorResponse
import com.kis.youranimelist.data.network.model.TokenResponse
import com.kis.youranimelist.data.network.model.UserResponse
import com.kis.youranimelist.data.network.model.personallist.AnimeStatusResponse
import com.kis.youranimelist.data.network.model.personallist.PersonalAnimeListResponse
import com.kis.youranimelist.data.network.model.rankingresponse.RankingRootResponse
import com.kis.youranimelist.data.network.model.searchresponse.SearchingRootResponse
import com.kis.youranimelist.data.network.model.suggestingresponse.SuggestingRootResponse
import com.kis.youranimelist.domain.model.ResultWrapper

interface RemoteDataSource {

    suspend fun getAccessToken(
        clientID: String,
        code: String,
        codeVerifier: String,
    ): ResultWrapper<TokenResponse>

    suspend fun refreshAccessToken(
        refreshToken: String,
    ): ResultWrapper<TokenResponse>

    suspend fun getUserData(): NetworkResponse<UserResponse, ErrorResponse>

    suspend fun getAnimeInfo(animeID: Int): NetworkResponse<AnimeResponse, ErrorResponse>
    suspend fun getAnimeRankingList(
        rankingType: String,
        limit: Int?,
        offset: Int?,
    ): NetworkResponse<RankingRootResponse, ErrorResponse>

    suspend fun getAnimeSuggestingList(
        limit: Int?,
        offset: Int?,
    ): NetworkResponse<SuggestingRootResponse, ErrorResponse>

    suspend fun getAnimeSearchList(
        search: String,
        limit: Int?,
        offset: Int?,
    ): NetworkResponse<SearchingRootResponse, ErrorResponse>

    suspend fun getPersonalAnimeList(
        status: String?,
        sort: String?,
        limit: Int,
        offset: Int,
    ): NetworkResponse<PersonalAnimeListResponse, ErrorResponse>

    suspend fun deletePersonalAnimeStatus(animeId: Int): NetworkResponse<Unit, ErrorResponse>
    suspend fun savePersonalAnimeStatus(
        animeId: Int,
        status: String?,
        score: Int?,
        episodesWatched: Int?,
    ): NetworkResponse<AnimeStatusResponse, ErrorResponse>

    suspend fun getPersonalAnimeStatus(animeId: Int): NetworkResponse<AnimeStatusResponse, ErrorResponse>
}
