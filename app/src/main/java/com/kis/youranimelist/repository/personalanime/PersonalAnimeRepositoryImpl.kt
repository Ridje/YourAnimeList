package com.kis.youranimelist.repository.personalanime

import com.kis.youranimelist.model.api.personal_list.PersonalAnimeListResponse
import com.kis.youranimelist.model.app.AnimeStatus
import com.kis.youranimelist.model.mapper.AnimeStatusMapper
import com.kis.youranimelist.repository.RemoteDataSource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PersonalAnimeRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val animeStatusMapper: AnimeStatusMapper,
) : PersonalAnimeRepository {

    override fun getAllDataProducer() = flow {
        var hasNext = true;
        val dataList = mutableListOf<AnimeStatus>()
        while (hasNext) {
            val fetchedValue = fetchData(1000, 0)
            if (fetchedValue.paging.next.isNullOrBlank()) {
                hasNext = false
            }
            dataList.addAll(fetchedValue.data.map { animeStatusMapper.map(it) })
        }
        emit(dataList.toList())
    }


    override suspend fun fetchData(limit: Int, offset: Int): PersonalAnimeListResponse {
        return remoteDataSource.getPersonalAnimeList(null, null, limit, offset)
    }
}
