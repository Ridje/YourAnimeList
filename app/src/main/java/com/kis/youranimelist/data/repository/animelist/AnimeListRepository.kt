package com.kis.youranimelist.data.repository.animelist

import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.network.model.ErrorResponse
import com.kis.youranimelist.data.repository.LocalDataSource
import com.kis.youranimelist.domain.rankinglist.mapper.AnimeMapper
import com.kis.youranimelist.domain.rankinglist.model.Anime

interface AnimeListRepository<T> {
    suspend fun fetchData(
        rankingType: String,
        limit: Int,
        offset: Int,
    ): NetworkResponse<T, ErrorResponse>

    suspend fun mapData(
        from: T,
    ): List<Anime>
}

suspend inline fun<T, S, SavedType> NetworkResponse<T, S>.saveToLocalDataSource(
    saver: suspend (SavedType) -> Unit,
    animeMapper: (T) -> List<SavedType>,
): NetworkResponse<T, S> {
    if (this is NetworkResponse.Success) {
        animeMapper.invoke(this.body).onEach { saver.invoke(it) }
    }
    return this
}
