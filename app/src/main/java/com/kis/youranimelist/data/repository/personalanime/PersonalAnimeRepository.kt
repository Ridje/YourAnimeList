package com.kis.youranimelist.data.repository.personalanime

import com.kis.youranimelist.data.network.model.personal_list.PersonalAnimeListResponse
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import kotlinx.coroutines.flow.Flow

interface PersonalAnimeRepository {
    fun getAllDataProducer(): Flow<List<AnimeStatus>>
    suspend fun fetchData(limit: Int, offset: Int): PersonalAnimeListResponse
    fun getPersonalAnimeStatusProducer(id: Int): Flow<AnimeStatus>
    suspend fun saveAnimeStatus(animeStatus: AnimeStatus): Boolean
}
