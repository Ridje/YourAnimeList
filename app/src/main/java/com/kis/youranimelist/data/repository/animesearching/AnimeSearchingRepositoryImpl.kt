package com.kis.youranimelist.data.repository.animesearching

import android.accounts.NetworkErrorException
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.cache.AnimeRankingMemoryCache
import com.kis.youranimelist.data.network.model.ErrorResponse
import com.kis.youranimelist.data.network.model.searchresponse.SearchingRootResponse
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.domain.rankinglist.model.Anime
import javax.inject.Inject

class AnimeSearchingRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val animeMapper: AnimeMapper,
    private val cacheFactory: AnimeRankingMemoryCache.Factory,
) : AnimeSearchingRepository {

    override suspend fun fetchData(
        rankingType: String,
        limit: Int,
        offset: Int,
    ): NetworkResponse<SearchingRootResponse, ErrorResponse> {
        val result = remoteDataSource.getAnimeSearchList(rankingType, limit, offset)
        if (result is NetworkResponse.Success) {
            result.body.data.map { animeMapper.map(it) }
                .onEach { localDataSource.saveAnimeToCache(it) }
        }
        return result
    }

    override fun getDataSource(search: String): PagingSource<Int, Anime> {
        cacheFactory.getOrCreate(search).invalidateCache()
        return object : PagingSource<Int, Anime>() {
            override fun getRefreshKey(state: PagingState<Int, Anime>): Int? {
                return state.anchorPosition
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Anime> {
                val nextPageKey = params.key ?: 0
                val cachedList = cacheFactory.getOrCreate(search).cache?.get(nextPageKey)
                return if (cachedList != null) {
                    LoadResult.Page(
                        data = cachedList,
                        prevKey = if (nextPageKey == 0) null else nextPageKey - cachedList.size,
                        nextKey = nextPageKey + cachedList.size
                    )
                } else {
                    when (val fetchedData = fetchData(search, params.loadSize, nextPageKey)) {
                        is NetworkResponse.Success -> {
                            fetchedData.body.data
                                .map { animeMapper.map(it) }
                                .onEach { localDataSource.saveAnimeToCache(it) }
                                .run {
                                    cacheFactory.getOrCreate(search)
                                        .updateCache(nextPageKey, this)
                                    LoadResult.Page(
                                        data = this,
                                        prevKey = if (nextPageKey == 0) null else nextPageKey - this.size,
                                        nextKey = nextPageKey + this.size
                                    )
                                }
                        }
                        is NetworkResponse.Error -> LoadResult.Error(fetchedData.error
                            ?: NetworkErrorException(fetchedData.body?.message))
                    }
                }
            }
        }

    }
}

