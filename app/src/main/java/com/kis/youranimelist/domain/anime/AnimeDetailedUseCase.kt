package com.kis.youranimelist.domain.anime

import com.kis.youranimelist.data.repository.anime.AnimeRepository
import com.kis.youranimelist.domain.rankinglist.model.Anime
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnimeDetailedUseCase @Inject constructor(
    private val animeRepository: AnimeRepository,
) {

    fun getAnimeDetailedProducer(animeId: Int): Flow<Anime> {
        return animeRepository.getAnimeDetailedDataSource(animeId)
    }

    suspend fun refreshAnimeDetailedData(animeId: Int) {
        animeRepository.refreshAnimeDetailedData(animeId)
    }
}
