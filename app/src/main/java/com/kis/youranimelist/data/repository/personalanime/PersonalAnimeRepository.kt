package com.kis.youranimelist.data.repository.personalanime

import com.kis.youranimelist.data.network.model.personal_list.PersonalAnimeListResponse
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import kotlinx.coroutines.flow.Flow

interface PersonalAnimeRepository {
    fun getPersonalAnimeStatusesProducer(): Flow<List<AnimeStatus>>
    fun getPersonalAnimeStatusProducer(id: Int): Flow<AnimeStatus?>

    suspend fun refreshPersonalAnimeStatuses()
    suspend fun deletePersonalAnimeStatus(animeId: Int): Boolean
    suspend fun refreshPersonalAnimeStatus(animeId: Int)
    suspend fun fetchData(limit: Int, offset: Int): PersonalAnimeListResponse
    suspend fun saveAnimeStatus(animeStatus: AnimeStatus): Boolean
}
