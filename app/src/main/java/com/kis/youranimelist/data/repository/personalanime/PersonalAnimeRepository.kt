package com.kis.youranimelist.data.repository.personalanime

import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.SyncRepository
import com.kis.youranimelist.data.network.model.ErrorResponse
import com.kis.youranimelist.data.network.model.personallist.PersonalAnimeItemResponse
import com.kis.youranimelist.data.network.model.personallist.PersonalAnimeListResponse
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import kotlinx.coroutines.flow.Flow

interface PersonalAnimeRepository : SyncRepository {
    fun getPersonalAnimeStatusesProducer(): Flow<List<AnimeStatus>>
    fun getPersonalAnimeStatusProducer(id: Int): Flow<AnimeStatus?>

    suspend fun deleteSyncPersonalData(): Boolean
    suspend fun deletePersonalAnimeStatus(animeId: Int): Boolean
    suspend fun refreshPersonalAnimeStatus(animeId: Int)
    suspend fun fetchData(limit: Int, offset: Int): NetworkResponse<PersonalAnimeListResponse, ErrorResponse>
    suspend fun fetchAllData(): List<PersonalAnimeItemResponse>
    suspend fun saveAnimeStatus(animeStatus: AnimeStatus): Boolean
}
