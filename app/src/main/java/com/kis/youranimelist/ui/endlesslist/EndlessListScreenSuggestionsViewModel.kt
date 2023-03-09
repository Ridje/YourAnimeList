package com.kis.youranimelist.ui.endlesslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.map
import com.kis.youranimelist.R
import com.kis.youranimelist.core.ResourceProvider
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.suggestions.SuggestionsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class EndlessListScreenSuggestionsViewModel @Inject constructor(
    suggestionsListUseCase: SuggestionsListUseCase,
    resourceProvider: ResourceProvider,
) : ViewModel(), EndlessListScreenContract.ScreenEventsListener {

    private val suggestionsPageSource: PagingSource<Int, Anime>

    init {
        suggestionsPageSource =
            suggestionsListUseCase.getSuggestionsListProducer()
    }

    private val _screenState: MutableStateFlow<EndlessListScreenContract.ScreenState> =
        MutableStateFlow(
            EndlessListScreenContract.ScreenState(
                items = Pager(PagingConfig(pageSize = 20, initialLoadSize = 20)) {
                    suggestionsPageSource
                }.flow.map { pagingData ->
                    pagingData.map { it.asEndlessListItem() }
                }.cachedIn(viewModelScope),
                resourceProvider.getString(R.string.suggestions),
            )
        )
    val screenState: StateFlow<EndlessListScreenContract.ScreenState>
        get() {
            return _screenState
        }

    override fun onReloadClicked(items: LazyPagingItems<EndlessListItem>) {
        items.retry()
    }

}
