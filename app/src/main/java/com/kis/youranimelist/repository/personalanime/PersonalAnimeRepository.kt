package com.kis.youranimelist.repository.personalanime

import com.kis.youranimelist.model.api.personal_list.PersonalAnimeListResponse
import com.kis.youranimelist.model.app.AnimeStatus
import kotlinx.coroutines.flow.Flow

interface PersonalAnimeRepository {
    fun getAllDataProducer(): Flow<List<AnimeStatus>>
    suspend fun fetchData(limit: Int, offset: Int): PersonalAnimeListResponse
}
