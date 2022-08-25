package com.kis.youranimelist.data.repository.animeranking

import androidx.paging.PagingSource
import com.haroldadmin.cnradapter.NetworkResponse
import com.kis.youranimelist.data.network.model.ErrorResponse
import com.kis.youranimelist.data.network.model.ranking_response.RankingRootResponse
import com.kis.youranimelist.domain.rankinglist.model.Anime

interface AnimeRankingRepository{
    suspend fun fetchData(rankingType: String, limit: Int, offset: Int): NetworkResponse<RankingRootResponse, ErrorResponse>
    fun getDataSource(rankingType: String): PagingSource<Int, Anime>
}
