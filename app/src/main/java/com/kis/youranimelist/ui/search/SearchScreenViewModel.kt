package com.kis.youranimelist.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.map
import com.kis.youranimelist.domain.searchlist.SearchListUseCase
import com.kis.youranimelist.ui.endlesslist.EndlessListScreenMapper
import com.kis.youranimelist.ui.endlesslist.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val searchUseCase: SearchListUseCase,
) : ViewModel(), SearchScreenContract.ScreenEventsListener {

    private val _screenState = MutableStateFlow(
        SearchScreenContract.ScreenState(
            "",
            null,
        )
    )
    val screenState = _screenState as StateFlow<SearchScreenContract.ScreenState>

    override fun onSearchClick(searchValue: String) {
        _screenState.value = _screenState.value.copy(
            items = Pager(PagingConfig(pageSize = 20,
                initialLoadSize = 20)) { searchUseCase.getSearchListProducer(searchValue) }.flow.map { pagingData ->
                pagingData.map {
                    EndlessListScreenMapper.map(it)
                }
            }.cachedIn(viewModelScope),
        )
    }

    override fun onSearchValueChanged(searchValue: String) {
        _screenState.value = _screenState.value.copy(
            searchValue = searchValue
        )
    }

    override fun onReloadClicked(items: LazyPagingItems<Item>) {
        items.retry()
    }
}