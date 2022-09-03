package com.kis.youranimelist.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.domain.explore.ExploreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    exploreUseCase: ExploreUseCase,
) : ViewModel() {

    private val _screenState: MutableStateFlow<ExploreScreenContract.ScreenState> =
        MutableStateFlow(
            exploreUseCase.getExploreScreenPagerSources()
                .asExploreScreenContractScreenState(viewModelScope)
        )

    val screenState: StateFlow<ExploreScreenContract.ScreenState>
        get() {
            return _screenState
        }
}
