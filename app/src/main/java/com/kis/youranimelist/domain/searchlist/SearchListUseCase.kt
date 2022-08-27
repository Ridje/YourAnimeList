package com.kis.youranimelist.domain.searchlist

import com.kis.youranimelist.data.repository.animesearching.AnimeSearchingRepository
import javax.inject.Inject


class SearchListUseCase @Inject constructor(
    private val animeSearchingRepository: AnimeSearchingRepository,
) {
    fun getSearchListProducer(search: String) =
        animeSearchingRepository.getDataSource(search)
}
