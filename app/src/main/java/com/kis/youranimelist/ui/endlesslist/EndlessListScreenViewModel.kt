package com.kis.youranimelist.ui.endlesslist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.map
import com.kis.youranimelist.core.ResourceProvider
import com.kis.youranimelist.domain.rankinglist.RankingListUseCase
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.ui.model.ExploreCategory
import com.kis.youranimelist.ui.navigation.InvalidNavArgumentException
import com.kis.youranimelist.ui.navigation.NavigationKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class EndlessListScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    rankingListUseCase: RankingListUseCase,
    resourceProvider: ResourceProvider,
) : ViewModel(), EndlessListScreenContract.ScreenEventsListener {

    private val rankingPageSource: PagingSource<Int, Anime>
    private val rankingType =
        ExploreCategory.Ranked.Factory.getByTag(savedStateHandle.get<String>(NavigationKeys.Argument.RANK)
            ?: throw InvalidNavArgumentException(NavigationKeys.Argument.RANK))
    private val title = resourceProvider.getString(rankingType.stringId)

    init {
        rankingPageSource =
            rankingListUseCase.getRankingListProducer(rankingType)
    }

    private val _screenState: MutableStateFlow<EndlessListScreenContract.ScreenState> =
        MutableStateFlow(
            EndlessListScreenContract.ScreenState(
                items = Pager(PagingConfig(pageSize = 20, initialLoadSize = 20)) {
                    rankingPageSource
                }.flow.map { pagingData ->
                    pagingData.map { it.asEndlessListItem() }
                }.cachedIn(viewModelScope),
                title,
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
