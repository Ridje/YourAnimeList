package com.kis.youranimelist.domain.explore

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.kis.youranimelist.domain.auth.AuthUseCase
import com.kis.youranimelist.domain.rankinglist.RankingListUseCase
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.suggestions.SuggestionsListUseCase
import com.kis.youranimelist.ui.model.ExploreCategory
import javax.inject.Inject

class ExploreUseCase @Inject constructor(
    private val rankingListUseCase: RankingListUseCase,
    private val suggestionsListUseCase: SuggestionsListUseCase,
    private val authUseCase: AuthUseCase,
) {
    fun getExploreScreenPagerSources(): List<Pair<ExploreCategory, Pager<Int, Anime>>> {
        return if (authUseCase.isClientAuth()) {
            listOf(
                rankedCategory(ExploreCategory.Ranked.TopRanked),
                rankedCategory(ExploreCategory.Ranked.Upcoming),
                rankedCategory(ExploreCategory.Ranked.Airing),
                suggestionsCategory(),
                rankedCategory(ExploreCategory.Ranked.Movies),
            )
        } else {
            return listOf(
                rankedCategory(ExploreCategory.Ranked.TopRanked),
                rankedCategory(ExploreCategory.Ranked.Upcoming),
                rankedCategory(ExploreCategory.Ranked.Airing),
                rankedCategory(ExploreCategory.Ranked.Movies),
            )
        }
    }

    private fun rankedCategory(rankedCategory: ExploreCategory.Ranked): Pair<ExploreCategory, Pager<Int, Anime>> {
        return rankedCategory to Pager(defaultPagingConfig()) {
            rankingListUseCase.getRankingListProducer(rankedCategory)
        }
    }

    private fun suggestionsCategory(): Pair<ExploreCategory, Pager<Int, Anime>> {
        return ExploreCategory.Suggestions to Pager(defaultPagingConfig()) {
            suggestionsListUseCase.getSuggestionsListProducer()
        }
    }

    private fun defaultPagingConfig(): PagingConfig {
        return PagingConfig(pageSize = 20, initialLoadSize = 20)
    }
}

