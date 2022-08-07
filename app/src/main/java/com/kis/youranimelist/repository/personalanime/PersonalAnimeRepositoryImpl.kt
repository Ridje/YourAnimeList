package com.kis.youranimelist.repository.personalanime

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kis.youranimelist.model.app.AnimeStatus
import com.kis.youranimelist.model.mapper.AnimeStatusMapper
import com.kis.youranimelist.repository.RemoteDataSource
import javax.inject.Inject

class PersonalAnimeRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val animeStatusMapper: AnimeStatusMapper,
) : PersonalAnimeRepository {
    override fun getDataSource(status: String): PagingSource<Int, AnimeStatus> {
        return object : PagingSource<Int, AnimeStatus>() {
            override fun getRefreshKey(state: PagingState<Int, AnimeStatus>): Int? {
                return state.anchorPosition
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AnimeStatus> {
                val nextPageKey = params.key ?: 0
                val animeStatusList =
                    fetchData(params.loadSize, nextPageKey)


                return LoadResult.Page(
                    data = animeStatusList,
                    prevKey = if (nextPageKey == 0) null else nextPageKey - animeStatusList.size,
                    nextKey = nextPageKey + animeStatusList.size
                )
            }

        }
    }

    override suspend fun fetchData(limit: Int, offset: Int): List<AnimeStatus> {
        return remoteDataSource.getUserAnime(null, null, limit, offset).data.map { animeStatusMapper.map(it) }
    }
}
