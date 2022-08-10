package com.kis.youranimelist.domain.rankinglist

import com.kis.youranimelist.data.repository.animeranking.AnimeRankingRepository
import javax.inject.Inject


class RankingListUseCase @Inject constructor(
    private val animeRankingRepository: AnimeRankingRepository,
) {
    fun getRankingListProducer(rankingType: String) =
        animeRankingRepository.getDataSource(rankingType = rankingType)
}
