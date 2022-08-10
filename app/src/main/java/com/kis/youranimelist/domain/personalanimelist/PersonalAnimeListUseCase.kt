package com.kis.youranimelist.domain.personalanimelist

import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.data.repository.personalanime.PersonalAnimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PersonalAnimeListUseCase @Inject constructor(
    private val personalAnimeRepository: PersonalAnimeRepository,
) {
    fun getPersonalAnimeListProducer(): Flow<List<AnimeStatus>> {
        return personalAnimeRepository.getAllDataProducer()
    }
}
