package com.kis.youranimelist.data.repository

import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.network.api.MyAnimeListAPI
import com.kis.youranimelist.data.network.api.MyAnimeListOAuthAPI
import com.kis.youranimelist.data.network.model.AnimeResponse
import com.kis.youranimelist.data.network.model.ErrorResponse
import com.kis.youranimelist.data.network.model.TokenResponse
import com.kis.youranimelist.data.network.model.UserResponse
import com.kis.youranimelist.data.network.model.personallist.AnimeStatusResponse
import com.kis.youranimelist.data.network.model.personallist.PersonalAnimeListResponse
import com.kis.youranimelist.di.Dispatcher
import com.kis.youranimelist.di.YALDispatchers
import com.kis.youranimelist.domain.model.ResultWrapper
import com.kis.youranimelist.domain.model.asResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteDataSourceImpl(
    private val malService: MyAnimeListAPI,
    private val malOauthService: MyAnimeListOAuthAPI,
    @Dispatcher(YALDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : RemoteDataSource {

    companion object {
        private const val USER_FIELDS =
            "id, name, picture, gender, birthday, location, joined_at, anime_statistics"
        private const val USER_ANIME_FIELDS =
            "list_status{tags,comments},media_type,num_episodes,mean,id,title,main_picture"
        private const val ANIME_FIELDS =
            "id, title, mean, main_picture, start_season, synopsis, genres, " +
                    "pictures, related_anime, media_type, num_episodes, recommendations, status"
        private const val REFRESH_TOKEN_GRANT_TYPE = "refresh_token"
        private const val ACCESS_TOKEN_GRANT_TYPE = "authorization_code"
    }

    override suspend fun getAnimeRankingList(
        rankingType: String,
        limit: Int?,
        offset: Int?,
    ) = withContext(ioDispatcher) {
        return@withContext malService.animeRanking(rankingType, limit, offset, ANIME_FIELDS)
    }

    override suspend fun getAnimeSuggestingList(
        limit: Int?,
        offset: Int?,
    ) = withContext(ioDispatcher) {
        return@withContext malService.animeSuggestions(limit, offset, ANIME_FIELDS)
    }

    override suspend fun getAnimeSearchList(
        search: String,
        limit: Int?,
        offset: Int?,
    ) = withContext(Dispatchers.IO) {
        return@withContext malService.animeSearching(search, limit, offset, ANIME_FIELDS)
    }

    override suspend fun getAnimeInfo(animeID: Int): NetworkResponse<AnimeResponse, ErrorResponse> {
        return withContext(ioDispatcher) { malService.animeDetails(animeID, ANIME_FIELDS) }
    }

    override suspend fun getAccessToken(
        clientID: String,
        code: String,
        codeVerifier: String,
    ): ResultWrapper<TokenResponse> {
        return malOauthService.getAccessToken(clientID, code, codeVerifier, ACCESS_TOKEN_GRANT_TYPE)
            .asResult()
    }

    override suspend fun refreshAccessToken(
        refreshToken: String,
    ): ResultWrapper<TokenResponse> {
        return malOauthService.refreshToken(REFRESH_TOKEN_GRANT_TYPE, refreshToken).asResult()
    }

    override suspend fun getUserData(): NetworkResponse<UserResponse, ErrorResponse> =
        malService.userProfile(USER_FIELDS)

    override suspend fun getPersonalAnimeList(
        status: String?,
        sort: String?,
        limit: Int,
        offset: Int,
    ): NetworkResponse<PersonalAnimeListResponse, ErrorResponse> {
        return withContext(ioDispatcher) {
            malService.userAnime(status, sort, limit, offset, USER_ANIME_FIELDS)
        }
    }

    override suspend fun deletePersonalAnimeStatus(animeId: Int) = withContext(ioDispatcher) {
        malService.deleteUserAnimeStatus(animeId)
    }

    override suspend fun savePersonalAnimeStatus(
        animeId: Int,
        status: String?,
        score: Int?,
        episodesWatched: Int?,
    ): NetworkResponse<AnimeStatusResponse, ErrorResponse> {
        return withContext(ioDispatcher) {
            malService.updateUserAnime(animeId, status, score, episodesWatched)
        }
    }

    override suspend fun getPersonalAnimeStatus(animeId: Int): NetworkResponse<AnimeStatusResponse, ErrorResponse> {
        return withContext(ioDispatcher) { malService.getUserAnimeStatus(animeId) }
    }
}
