package com.kis.youranimelist.data.repository.pagingsource

import androidx.paging.PagingSource
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.ui.model.CachingKey

interface AnimeListPagingRepository {
    fun getDataSource(key: CachingKey): PagingSource<Int, Anime>
}
