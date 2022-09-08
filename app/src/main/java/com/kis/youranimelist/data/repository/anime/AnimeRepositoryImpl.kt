package com.kis.youranimelist.data.repository.anime

import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.cache.AnimeRankingMemoryCache
import com.kis.youranimelist.data.cache.model.PicturePersistence
import com.kis.youranimelist.data.network.model.rankingresponse.RankingRootResponse
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.domain.model.ResultWrapper
import com.kis.youranimelist.domain.model.asResult
import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.ui.model.ExploreCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class AnimeRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val animeMapper: AnimeMapper,
    private val cache: AnimeRankingMemoryCache.Factory,
) : AnimeRepository {

    override fun getRankingAnimeListProducer(
        rankingType: ExploreCategory.Ranked,
        limit: Int?,
        offset: Int?,
    ): Flow<ResultWrapper<List<Anime>>> = flow {
        cache.getOrCreate(rankingType).cache?.get(offset ?: 0)?.let { cachedResult ->
            emit(ResultWrapper.Success(cachedResult))
        }

        val remoteResult = remoteDataSource
            .getAnimeRankingList(rankingType.tag, limit, offset)
            .asResult { from: RankingRootResponse -> from.data.map { animeMapper.map(it) } }
            .also { remoteResult ->
                if (remoteResult is ResultWrapper.Success) {
                    cache.getOrCreate(rankingType).updateCache(offset ?: 0, remoteResult.data)
                    remoteResult.data.forEach { localDataSource.saveAnimeToCache(it) }
                }
            }
        emit(remoteResult)
    }

    override suspend fun getRankingAnimeList(
        rankingType: ExploreCategory.Ranked,
        limit: Int?,
        offset: Int?,
    ): ResultWrapper<List<Anime>> {

        cache.getOrCreate(rankingType).cache?.get(offset ?: 0)?.let { cachedResult ->
            return ResultWrapper.Success(cachedResult)
        }

        return remoteDataSource
            .getAnimeRankingList(rankingType.tag, limit, offset)
            .asResult { from: RankingRootResponse -> from.data.map { animeMapper.map(it) } }
            .also { remoteResult ->
                if (remoteResult is ResultWrapper.Success) {
                    cache.getOrCreate(rankingType).updateCache(offset ?: 0, remoteResult.data)
                    remoteResult.data.forEach { localDataSource.saveAnimeToCache(it) }
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAnimeDetailedDataSource(animeID: Int) =
        localDataSource.getAnimeDetailedDataProducer(animeID)
            .filterNotNull()
            .flatMapLatest { animeDetailedData ->
                flow {
                    val relatedAnimeResults = mutableListOf<PicturePersistence?>()
                    for (relatedAnime in animeDetailedData.relatedAnime) {
                        val pictureResult = relatedAnime.pictureId?.let { pictureId ->
                            localDataSource.getPictureById(pictureId)
                        }
                        relatedAnimeResults.add(pictureResult)
                    }
                    val recommendedAnimeResults = mutableListOf<PicturePersistence?>()
                    for (recommendedAnime in animeDetailedData.recommendedAnime) {
                        val pictureResult = recommendedAnime.pictureId?.let { pictureId ->
                            localDataSource.getPictureById(pictureId)
                        }
                        recommendedAnimeResults.add(pictureResult)
                    }

                    val enrichedRelatedAndRecommendedResult =
                        animeMapper.map(animeDetailedData,
                            relatedAnimeResults.toList(),
                            recommendedAnimeResults.toList())
                    emit(enrichedRelatedAndRecommendedResult)
                }
            }

    override suspend fun refreshAnimeDetailedData(animeID: Int): Boolean {
        val remoteResult = remoteDataSource.getAnimeInfo(animeID)
        if (remoteResult is NetworkResponse.Success) {
            localDataSource.saveAnimeToCache(Anime(remoteResult.body))
            return true
        }
        return false
    }
}
