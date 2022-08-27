package com.kis.youranimelist.domain.rankinglist

import com.kis.youranimelist.data.repository.pagingsource.AnimeListPagingRepository
import com.kis.youranimelist.di.Ranking
import com.kis.youranimelist.ui.model.AnimeRankType
import com.kis.youranimelist.ui.model.CachingKey
import javax.inject.Inject

class RankingListUseCase @Inject constructor(
    @Ranking
    private val animePagingSourceRepository: AnimeListPagingRepository,
) {
    fun getRankingListProducer(rankingType: AnimeRankType) =
        animePagingSourceRepository.getDataSource(rankingType)
}
