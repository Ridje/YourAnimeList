package com.kis.youranimelist.domain

import com.kis.youranimelist.model.app.AnimeStatus
import com.kis.youranimelist.repository.personalanime.PersonalAnimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PersonalAnimeListUseCase @Inject constructor(
    private val personalAnimeRepository: PersonalAnimeRepository,
) {
    fun getPersonalAnimeListProducer(): Flow<List<AnimeStatus>> {
        return personalAnimeRepository.getAllDataProducer()
    }
}
