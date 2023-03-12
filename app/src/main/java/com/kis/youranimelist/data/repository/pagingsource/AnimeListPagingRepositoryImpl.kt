package com.kis.youranimelist.data.repository.pagingsource

import android.accounts.NetworkErrorException
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.cache.AnimeRankingMemoryCache
import com.kis.youranimelist.data.repository.animelist.AnimeListRepository
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.ui.model.CachingKey

class AnimeListPagingRepositoryImpl<T>(
    private val cacheFactory: AnimeRankingMemoryCache.Factory,
    private val dataFetcher: AnimeListRepository<T>,
) : AnimeListPagingRepository, AnimeListRepository<T> by dataFetcher {
    override fun getDataSource(key: CachingKey): PagingSource<Int, Anime> {
        cacheFactory.getOrCreate(key).invalidateCache()
        return object : PagingSource<Int, Anime>() {
            override fun getRefreshKey(state: PagingState<Int, Anime>): Int? {
                return state.anchorPosition
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Anime> {
                val nextPageKey = params.key ?: 0
                val cachedList = cacheFactory.getOrCreate(key).cache?.get(nextPageKey)
                return if (cachedList != null) {
                    LoadResult.Page(
                        data = cachedList,
                        prevKey = if (nextPageKey == 0) null else nextPageKey - cachedList.size,
                        nextKey = nextPageKey + cachedList.size
                    )
                } else {
                    when (val fetchedData =
                        fetchData(key.toRequest(), params.loadSize, nextPageKey)) {
                        is NetworkResponse.Success -> {
                            mapData(fetchedData.body).run {
                                cacheFactory.getOrCreate(key)
                                    .updateCache(nextPageKey, this)
                                LoadResult.Page(
                                    data = this,
                                    prevKey = if (nextPageKey == 0) null else nextPageKey - this.size,
                                    nextKey = if (this.isEmpty()) null else nextPageKey + this.size
                                )
                            }
                        }
                        is NetworkResponse.Error -> LoadResult.Error(
                            fetchedData.error ?: NetworkErrorException(fetchedData.body?.message)
                        )
                    }
                }
            }
        }
    }
}
