package com.kis.youranimelist.data.repository.animesearching

import androidx.paging.PagingSource
import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.network.model.ErrorResponse
import com.kis.youranimelist.data.network.model.searchresponse.SearchingRootResponse
import com.kis.youranimelist.domain.rankinglist.model.Anime

interface AnimeSearchingRepository {

    suspend fun fetchData(
        rankingType: String,
        limit: Int,
        offset: Int,
    ): NetworkResponse<SearchingRootResponse, ErrorResponse>

    fun getDataSource(search: String): PagingSource<Int, Anime>
}
