package com.kis.youranimelist.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.core.ResourceProvider
import com.kis.youranimelist.domain.explore.ExploreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    exploreUseCase: ExploreUseCase,
    resourceProvider: ResourceProvider,
) : ViewModel() {

    private val _screenState: StateFlow<ExploreScreenContract.ScreenState> =
        exploreUseCase.getRefreshListsSource().mapLatest {
            exploreUseCase.getExploreScreenPagerSources()
                .asExploreScreenContractScreenState(viewModelScope, resourceProvider)
        }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            exploreUseCase.getExploreScreenPagerSources()
                .asExploreScreenContractScreenState(viewModelScope, resourceProvider)
        )

    val screenState: StateFlow<ExploreScreenContract.ScreenState> = _screenState
}
