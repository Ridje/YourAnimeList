package com.kis.youranimelist.data.repository.personalanime

import com.kis.youranimelist.data.network.model.personal_list.PersonalAnimeListResponse
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.personalanimelist.mapper.AnimeStatusMapper
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.data.repository.RemoteDataSource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PersonalAnimeRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val animeStatusMapper: AnimeStatusMapper,
) : PersonalAnimeRepository {

    override fun getAllDataProducer() = flow {
        val personalAnimeLocalCache = localDataSource.getPersonalAnimeStatusFromCache()
        if (personalAnimeLocalCache.isNotEmpty()) {
            emit(personalAnimeLocalCache.map { animeStatusMapper.map(it) })
        }
        var hasNext = true;
        val dataList = mutableListOf<AnimeStatus>()
        while (hasNext) {
            val fetchedValue = fetchData(1000, 0)
            if (fetchedValue.paging.next.isNullOrBlank()) {
                hasNext = false
            }
            dataList.addAll(fetchedValue.data.map { animeStatusMapper.map(it) })
        }
        localDataSource.savePersonalAnimeStatusToCache(dataList)
        emit(dataList)
    }


    override suspend fun fetchData(limit: Int, offset: Int): PersonalAnimeListResponse {
        return remoteDataSource.getPersonalAnimeList(null, null, limit, offset)
    }
}
