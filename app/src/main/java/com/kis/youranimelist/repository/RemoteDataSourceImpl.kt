package com.kis.youranimelist.repository

import android.accounts.NetworkErrorException
import com.kis.youranimelist.model.api.AnimeResponse
import com.kis.youranimelist.model.api.Token
import com.kis.youranimelist.model.api.UserResponse
import com.kis.youranimelist.model.api.ranking_response.AnimeRanked
import com.kis.youranimelist.model.mapper.UserMapper
import com.kis.youranimelist.network.MyAnimeListAPI
import com.kis.youranimelist.network.MyAnimeListOAuthAPI


class RemoteDataSourceImpl(
    private val malService: MyAnimeListAPI,
    private val malOauthService: MyAnimeListOAuthAPI,
    private val userMapper: UserMapper,
) : RemoteDataSource {

    companion object {
        private const val USER_FIELDS = "id, name, picture, gender, birthday, location, joined_at, anime_statistics"
    }

    override fun getAnimeRankingList(
        rankingType: String,
        limit: Int?,
        offset: Int?,
        fields: String?,
    ): List<AnimeRanked> {

        val result = malService.animeRanking(rankingType, limit, offset, fields).execute()

        return if (result.isSuccessful) {
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
    ): Token {

        val result =
            malOauthService.getAccessToken(clientID, code, codeVerifier, grantType).execute()

        return when {
            result.isSuccessful -> result.body() ?: throw NetworkErrorException()
            else -> throw NetworkErrorException()
        }
    }

    override fun getUserData(): UserResponse {

        val result = malService.userProfile(USER_FIELDS).execute()
        return when {
            result.isSuccessful -> result.body()
                ?: throw NetworkErrorException()
            else -> throw NetworkErrorException()
        }
    }
}
