package com.kis.youranimelist.ui.endlesslist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.map
import com.kis.youranimelist.NavigationKeys
import com.kis.youranimelist.domain.RankingListUseCase
import com.kis.youranimelist.model.app.Anime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class EndlessListScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    rankingListUseCase: RankingListUseCase,
) : ViewModel() {

    private val rankingPageSource: PagingSource<Int, Anime>

    init {
        rankingPageSource =
            rankingListUseCase.getRankingListProducer(
                savedStateHandle.get<String>(NavigationKeys.Argument.RANK) ?: throw RuntimeException("WTF")
            )
    }

    val screenState: MutableStateFlow<EndlessListScreenContract.ScreenState> = MutableStateFlow(
        EndlessListScreenContract.ScreenState(
            items = Pager(PagingConfig(pageSize = 20, initialLoadSize = 20)) {
                rankingPageSource
            }.flow.map { pagingData ->
                pagingData.map {
                    EndlessListScreenMapper.map(it)
                }
            }
        )
    )
}
