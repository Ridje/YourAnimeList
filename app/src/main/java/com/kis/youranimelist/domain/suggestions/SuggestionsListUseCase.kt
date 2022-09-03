package com.kis.youranimelist.domain.suggestions

import com.kis.youranimelist.data.repository.pagingsource.AnimeListPagingRepository
import com.kis.youranimelist.di.Suggestions
import com.kis.youranimelist.ui.model.ExploreCategory
import javax.inject.Inject

class SuggestionsListUseCase @Inject constructor(
    @Suggestions
    private val animePagingSourceRepository: AnimeListPagingRepository,
) {
    fun getSuggestionsListProducer() =
        animePagingSourceRepository.getDataSource(ExploreCategory.Suggestions)
}
