package com.kis.youranimelist.ui.search

import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import com.kis.youranimelist.ui.endlesslist.Item
import kotlinx.coroutines.flow.Flow

object SearchScreenContract {

    data class ScreenState(
        val searchValue: String,
        val items: Flow<PagingData<Item>>?,
    )

    sealed class Effect {
        object SearchError: Effect()
    }

    interface ScreenEventsListener {
        fun onSearchClick(searchValue: String): Boolean
        fun onSearchValueChanged(searchValue: String)
        fun onReloadClicked(items: LazyPagingItems<Item>)
    }
}
