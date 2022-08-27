package com.kis.youranimelist.domain.searchlist

import com.kis.youranimelist.data.repository.pagingsource.AnimeListPagingRepository
import com.kis.youranimelist.di.Search
import com.kis.youranimelist.ui.model.CachingKey
import javax.inject.Inject


class SearchListUseCase @Inject constructor(
    @Search
    private val animeListPagingRepository: AnimeListPagingRepository,
) {
    fun getSearchListProducer(search: String) =
        animeListPagingRepository.getDataSource(
            StringCachingKey(search)
        )
}

data class StringCachingKey(
    val key: String,
) : CachingKey {
    override fun toRequest(): String {
        return key
    }
}
