package com.kis.youranimelist.domain.rankinglist

import com.kis.youranimelist.data.repository.pagingsource.AnimeListPagingRepository
import com.kis.youranimelist.di.Ranking
import com.kis.youranimelist.ui.model.ExploreCategory
import javax.inject.Inject

class RankingListUseCase @Inject constructor(
    @Ranking
    private val animePagingSourceRepository: AnimeListPagingRepository,
) {
    fun getRankingListProducer(rankingType: ExploreCategory.Ranked) =
        animePagingSourceRepository.getDataSource(rankingType)
}
