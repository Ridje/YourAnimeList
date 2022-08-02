package com.kis.youranimelist.repository

import com.kis.youranimelist.model.api.Token
import com.kis.youranimelist.model.app.Anime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AnimeRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
) : AnimeRepository {

    override val animeCache: HashMap<Int, Anime> = HashMap()

    override fun getRankingAnimeList(
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
    ): Token {
        return remoteDataSource.getAccessToken(clientID, code, codeVerifier, grantType)
    }
}
