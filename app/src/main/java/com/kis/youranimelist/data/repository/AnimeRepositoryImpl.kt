package com.kis.youranimelist.data.repository

import com.kis.youranimelist.data.network.model.TokenResponse
import com.kis.youranimelist.domain.rankinglist.model.Anime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AnimeRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
) : AnimeRepository {

    override val animeCache: HashMap<Int, Anime> = HashMap()

    override suspend fun getRankingAnimeList(
        rankingType: String,
        limit: Int?,
        offset: Int?,
        fields: String?,
    ): List<Anime> {

        val result = remoteDataSource.getAnimeRankingList(rankingType, limit, offset, fields)
        animeCache.putAll(
            result.associate { it.anime.id to Anime(it.anime) }
        )
        return result.map { Anime(it.anime) }
    }

    override fun getAnimeDetailedData(animeID: Int, keys: String?): Flow<Anime?> {
        return flow {
            emit(animeCache[animeID])
            val result = remoteDataSource.getAnimeInfo(animeID, keys)
            animeCache[result.id] = Anime(result)
            result.relatedAnime?.let { relatedAnime ->
                for (anime in relatedAnime) {
                    animeCache[anime.node.id] = Anime(anime.node)
                }
            }
            emit(animeCache[animeID])
        }
    }

    override fun getAccessToken(
        clientID: String,
        code: String,
        codeVerifier: String,
        grantType: String,
    ): TokenResponse {
        return remoteDataSource.getAccessToken(clientID, code, codeVerifier, grantType)
    }

    override fun getFavouriteAnime(): Anime? {
        return animeCache.values.randomOrNull()
    }
}
