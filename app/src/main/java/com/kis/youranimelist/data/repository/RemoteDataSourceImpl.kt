package com.kis.youranimelist.data.repository

import android.accounts.NetworkErrorException
import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.network.api.MyAnimeListAPI
import com.kis.youranimelist.data.network.api.MyAnimeListOAuthAPI
import com.kis.youranimelist.data.network.model.AnimeResponse
import com.kis.youranimelist.data.network.model.ErrorResponse
import com.kis.youranimelist.data.network.model.TokenResponse
import com.kis.youranimelist.data.network.model.UserResponse
import com.kis.youranimelist.data.network.model.personallist.AnimeStatusResponse
import com.kis.youranimelist.data.network.model.personallist.PersonalAnimeListResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class RemoteDataSourceImpl(
    private val malService: MyAnimeListAPI,
    private val malOauthService: MyAnimeListOAuthAPI,
    private val dispatchers: Dispatchers,
) : RemoteDataSource {

    companion object {
        private const val USER_FIELDS =
            "id, name, picture, gender, birthday, location, joined_at, anime_statistics"
        private const val USER_ANIME_FIELDS =
            "id, title, main_picture, list_status, media_type, num_episodes, mean"
        private const val ANIME_FIELDS =
            "id, title, mean, main_picture, start_season, synopsis, genres, pictures, related_anime, media_type, num_episodes"

    }

    override suspend fun getAnimeRankingList(
        rankingType: String,
        limit: Int?,
        offset: Int?,
    ) = withContext(Dispatchers.IO) {
        return@withContext malService.animeRanking(rankingType, limit, offset, ANIME_FIELDS)
    }

    override suspend fun getAnimeSearchList(
        search: String,
        limit: Int?,
        offset: Int?,
    ) = withContext(Dispatchers.IO) {
        return@withContext malService.animeSearching(search, limit, offset, ANIME_FIELDS)
    }

    override suspend fun getAnimeInfo(animeID: Int): AnimeResponse? {
        return withContext(dispatchers.IO) {
            try {
                val result = malService.animeDetails(animeID, ANIME_FIELDS).execute()

                if (result.isSuccessful) {
                    result.body()
                } else {
                    return@withContext null
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }

                return@withContext null
            }
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

    override suspend fun getUserData(): NetworkResponse<UserResponse, ErrorResponse> =
        malService.userProfile(USER_FIELDS)

    override suspend fun getPersonalAnimeList(
        status: String?,
        sort: String?,
        limit: Int,
        offset: Int,
    ): NetworkResponse<PersonalAnimeListResponse, ErrorResponse> {
        return withContext(dispatchers.IO) {

            malService.userAnime(status, sort, limit, offset, USER_ANIME_FIELDS)

        }
    }

    override suspend fun deletePersonalAnimeStatus(animeId: Int) = withContext(dispatchers.IO) {
        malService.deleteUserAnimeStatus(animeId)
    }

    override suspend fun savePersonalAnimeStatus(
        animeId: Int,
        status: String?,
        score: Int?,
        episodesWatched: Int?,
    ): NetworkResponse<AnimeStatusResponse, ErrorResponse> {
        return withContext(dispatchers.IO) {
            malService.updateUserAnime(animeId, status, score, episodesWatched)
        }
    }

    override suspend fun getPersonalAnimeStatus(animeId: Int): AnimeStatusResponse? {
        return withContext(dispatchers.IO) {
            try {
                val result =
                    malService.getUserAnimeStatus(animeId).execute()
                if (result.isSuccessful) {
                    return@withContext result.body()
                } else {
                    return@withContext null
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                return@withContext null
            }
        }
    }
}
