package com.kis.youranimelist.repository.animeranking

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kis.youranimelist.model.app.Anime
import com.kis.youranimelist.model.mapper.AnimeMapper
import com.kis.youranimelist.repository.RemoteDataSource
import javax.inject.Inject

class AnimeRankingRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val animeMapper: AnimeMapper,
) : AnimeRankingRepository {

    companion object {
        private const val fields =
            "id, title, main_picture, synopsis, genres, start_season, mean, media_type, num_episodes"
    }

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
        return remoteDataSource.getAnimeRankingList(rankingType, limit, offset, fields)
            .map { animeMapper.map(it) }.toList()
    }
}

