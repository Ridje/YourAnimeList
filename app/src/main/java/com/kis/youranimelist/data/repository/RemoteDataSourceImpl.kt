package com.kis.youranimelist.data.repository

import android.accounts.NetworkErrorException
import com.kis.youranimelist.data.network.api.MyAnimeListAPI
import com.kis.youranimelist.data.network.api.MyAnimeListOAuthAPI
import com.kis.youranimelist.data.network.model.AnimeResponse
import com.kis.youranimelist.data.network.model.TokenResponse
import com.kis.youranimelist.data.network.model.UserResponse
import com.kis.youranimelist.data.network.model.personal_list.PersonalAnimeListResponse
import com.kis.youranimelist.data.network.model.ranking_response.AnimeRankedResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class RemoteDataSourceImpl(
    private val malService: MyAnimeListAPI,
    private val malOauthService: MyAnimeListOAuthAPI,
) : RemoteDataSource {

    companion object {
        private const val USER_FIELDS =
            "id, name, picture, gender, birthday, location, joined_at, anime_statistics"
        private const val USER_ANIME_FIELDS =
            "id, title, main_picture, list_status, media_type, num_episodes, mean"
    }

    override suspend fun getAnimeRankingList(
        rankingType: String,
        limit: Int?,
        offset: Int?,
        fields: String?,
    ): List<AnimeRankedResponse> = withContext(Dispatchers.IO) {
        val result = malService.animeRanking(rankingType, limit, offset, fields).execute()

        if (result.isSuccessful) {
            result.body()?.data ?: throw NetworkErrorException("Request wasn't successful")
        } else {
            throw NetworkErrorException()
        }
    }


    override fun getAnimeInfo(animeID: Int, keys: String?): AnimeResponse {

        val result = malService.animeDetails(animeID, keys).execute()

        return if (result.isSuccessful) {
            result.body() ?: throw NetworkErrorException("Request wasn't successful")
        } else {
            throw NetworkErrorException("Request wasn't successful")
        }
    }

    override fun getAccessToken(
        clientID: String,
        code: String,
        codeVerifier: String,
        grantType: String,
    ): TokenResponse {

        val result =
            malOauthService.getAccessToken(clientID, code, codeVerifier, grantType).execute()

        return when {
            result.isSuccessful -> result.body() ?: throw NetworkErrorException()
            else -> throw NetworkErrorException()
        }
    }

    override fun getUserData(): UserResponse? {
        try {
            val result = malService.userProfile(USER_FIELDS).execute()
            return when {
                result.isSuccessful -> result.body()
                    ?: throw NetworkErrorException()
                else -> throw NetworkErrorException()
            }
        } catch (e: Exception) {
            return null
        }
    }

    override suspend fun getPersonalAnimeList(
        status: String?,
        sort: String?,
        limit: Int,
        offset: Int,
    ): PersonalAnimeListResponse {
        return withContext(Dispatchers.IO) {
            val result =
                malService.userAnime(status, sort, limit, offset, USER_ANIME_FIELDS).execute()
            if (result.isSuccessful) {
                result.body() ?: throw NetworkErrorException("Request wasn't successful")
            } else {
                throw NetworkErrorException()
            }
        }
    }
}
