package com.kis.youranimelist.data.repository.anime

import com.kis.youranimelist.data.cache.model.PicturePersistence
import com.kis.youranimelist.data.network.model.TokenResponse
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.domain.rankinglist.model.Anime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class AnimeRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val animeMapper: AnimeMapper,
) : AnimeRepository {

    override suspend fun getRankingAnimeList(
        rankingType: String,
        limit: Int?,
        offset: Int?,
    ): List<Anime> {

        val result = remoteDataSource.getAnimeRankingList(rankingType, limit, offset)

        val convertedResult = result.map { Anime(it.anime) }
        for (anime in convertedResult) {
            localDataSource.saveAnimeToCache(anime)
        }

        return convertedResult
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAnimeDetailedDataSource(animeID: Int) =
        localDataSource.getAnimeDetailedDataProducerFromCache(animeID)
            .flatMapLatest { animeDetailedData ->
                if (animeDetailedData == null) {
                    remoteDataSource.getAnimeInfo(animeID)
                }
                flow {
                    animeDetailedData?.let {
                        val relatedAnimeResults = mutableListOf<PicturePersistence?>()
                        for (relatedAnime in animeDetailedData.relatedAnime) {
                            val pictureResult = relatedAnime.pictureId?.let { pictureId ->
                                localDataSource.getRelatedAnimeMainPicture(pictureId)
                            }

                            relatedAnimeResults.add(pictureResult)
                        }
                        val result =
                            animeMapper.map(animeDetailedData, relatedAnimeResults.toList())
                        emit(result)
                    }
                }
            }

    override suspend fun refreshAnimeDetailedData(animeID: Int): Boolean {
        val remoteResult = remoteDataSource.getAnimeInfo(animeID)
        remoteResult?.let {
            localDataSource.saveAnimeToCache(Anime(remoteResult))
            return true
        } ?: return false
    }

    override suspend fun getAccessToken(
        clientID: String,
        code: String,
        codeVerifier: String,
        grantType: String,
    ): TokenResponse {
        return remoteDataSource.getAccessToken(clientID, code, codeVerifier, grantType)
    }
}
