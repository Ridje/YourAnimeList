package com.kis.youranimelist.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.core.ResourceProvider
import com.kis.youranimelist.domain.explore.ExploreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    exploreUseCase: ExploreUseCase,
    resourceProvider: ResourceProvider,
) : ViewModel() {

    private val _screenState: MutableStateFlow<ExploreScreenContract.ScreenState> =
        MutableStateFlow(
            exploreUseCase.getExploreScreenPagerSources()
                .asExploreScreenContractScreenState(viewModelScope, resourceProvider)
        )

    val screenState: StateFlow<ExploreScreenContract.ScreenState>
        get() {
            return _screenState
        }
}
