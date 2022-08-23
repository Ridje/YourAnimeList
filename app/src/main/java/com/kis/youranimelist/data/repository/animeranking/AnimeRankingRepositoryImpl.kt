package com.kis.youranimelist.data.repository.animeranking

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.data.repository.RemoteDataSource
import javax.inject.Inject

class AnimeRankingRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val animeMapper: AnimeMapper,
) : AnimeRankingRepository {

    override fun getDataSource(rankingType: String): PagingSource<Int, Anime> {
        return object : PagingSource<Int, Anime>() {
            override fun getRefreshKey(state: PagingState<Int, Anime>): Int? {
                return state.anchorPosition
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Anime> {
                val nextPageKey = params.key ?: 0
                val animeList =
                    fetchData(rankingType, params.loadSize, nextPageKey)
                return LoadResult.Page(
                    data = animeList,
                    prevKey = if (nextPageKey == 0) null else nextPageKey - animeList.size,
                    nextKey = nextPageKey + animeList.size
                )
            }

        }

    }

    override suspend fun fetchData(rankingType: String, limit: Int, offset: Int): List<Anime> {
        return remoteDataSource.getAnimeRankingList(rankingType, limit, offset)
            .map { animeMapper.map(it) }.onEach { localDataSource.saveAnimeToCache(it) }.toList()
    }
}

