package com.kis.youranimelist.domain.explore

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.kis.youranimelist.data.repository.pagingsource.AnimeListPagingRepository
import com.kis.youranimelist.di.Ranking
import com.kis.youranimelist.ui.model.AnimeRankType
import javax.inject.Inject

class ExploreUseCase @Inject constructor(
    @Ranking
    private val animeListPagingRepository: AnimeListPagingRepository,
) {
    fun getExploreScreenPagerSources() = listOf(
        AnimeRankType.Airing to Pager(PagingConfig(pageSize = 20, initialLoadSize = 20)) {
            animeListPagingRepository.getDataSource(AnimeRankType.Airing)
        },
        AnimeRankType.Upcoming to Pager(PagingConfig(pageSize = 20, initialLoadSize = 20)) {
            animeListPagingRepository.getDataSource(AnimeRankType.Upcoming)
        },
        AnimeRankType.TopRanked to Pager(PagingConfig(pageSize = 20, initialLoadSize = 20)) {
            animeListPagingRepository.getDataSource(AnimeRankType.TopRanked)
        },
    )
}
