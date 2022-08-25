package com.kis.youranimelist.data.repository.animeranking

import android.accounts.NetworkErrorException
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.network.model.ErrorResponse
import com.kis.youranimelist.data.network.model.ranking_response.RankingRootResponse
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.domain.rankinglist.model.Anime
import java.lang.RuntimeException
import javax.inject.Inject

class AnimeRankingRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val animeMapper: AnimeMapper,
) : AnimeRankingRepository {

    private val listCache = HashMap<Int, List<Anime>>()

    override fun getDataSource(rankingType: String): PagingSource<Int, Anime> {
        return object : PagingSource<Int, Anime>() {
            override fun getRefreshKey(state: PagingState<Int, Anime>): Int? {
                return state.anchorPosition
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Anime> {
                val nextPageKey = params.key ?: 0
                val cachedList = listCache[nextPageKey]
                return if ( cachedList != null) {
                    LoadResult.Page(
                        data = cachedList,
                        prevKey = if (nextPageKey == 0) null else nextPageKey - cachedList.size,
                        nextKey = nextPageKey + cachedList.size
                    )
                } else {
                    when (val fetchedData = fetchData(rankingType, params.loadSize, nextPageKey)) {
                        is NetworkResponse.Success -> {
                            fetchedData.body.data
                                .map { animeMapper.map(it) }
                                .onEach { localDataSource.saveAnimeToCache(it) }
                                .run {
                                    listCache[nextPageKey] = this
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

    override suspend fun fetchData(
        rankingType: String,
        limit: Int,
        offset: Int,
    ): NetworkResponse<RankingRootResponse, ErrorResponse> {
        return remoteDataSource.getAnimeRankingList(rankingType, limit, offset)
    }
}

