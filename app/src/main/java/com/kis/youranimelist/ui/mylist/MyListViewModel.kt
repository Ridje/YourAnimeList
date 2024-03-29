package com.kis.youranimelist.ui.mylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.core.utils.SortType
import com.kis.youranimelist.domain.model.ResultWrapper
import com.kis.youranimelist.domain.personalanimelist.PersonalAnimeListUseCase
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.settings.SettingsUseCase
import com.kis.youranimelist.domain.user.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyListViewModel @Inject constructor(
    private val personalAnimeListUseCase: PersonalAnimeListUseCase,
    private val settingsUseCase: SettingsUseCase,
    userUseCase: UserUseCase,
) : ViewModel(), MyListScreenContract.ScreenEventsListener {

    private val animeStatusesSource: Flow<ResultWrapper<List<AnimeStatus>>> =
        personalAnimeListUseCase.getPersonalAnimeStatusesProducer()

    private val _effectStream: MutableSharedFlow<MyListScreenContract.Effect> =
        MutableSharedFlow()
    val effectStream: SharedFlow<MyListScreenContract.Effect>
        get() = _effectStream

    private val _screenState: MutableStateFlow<MyListScreenContract.ScreenState> = MutableStateFlow(
        MyListScreenContract.ScreenState(
            isLoading = false,
            isSwipeToRefreshTurnedOn = !userUseCase.isAppAuthorization(),
            items = listOf(),
        )
    )
    val screenState: StateFlow<MyListScreenContract.ScreenState>
        get() = _screenState

    init {
        startObserveMyListChanges()
        getCurrentSort()
    }

    private fun getCurrentSort() {
        viewModelScope.launch {
            _screenState.update {
                it.copy(
                    sortBy = settingsUseCase.settingPersonalListSort()
                )
            }
        }
    }

    private fun startObserveMyListChanges() {
        viewModelScope.launch {
            _screenState.value = _screenState.value.copy(
                isLoading = false,
                isError = false,
            )
            animeStatusesSource
                .flowOn(Dispatchers.IO)
                .collectLatest { result ->
                    val newValue = when (result) {
                        is ResultWrapper.Success -> {
                            _screenState.value.copy(
                                isError = false,
                                items = result.data
                                    .map { it.asMyListItem() }
                            )
                        }
                        is ResultWrapper.Error -> {
                            _screenState.value.copy(
                                isLoading = false,
                                isError = true,
                            )
                        }
                        ResultWrapper.Loading -> {
                            _screenState.value.copy(
                                isLoading = true,
                                isError = false,
                            )
                        }
                    }
                    _screenState.value = newValue
                }
        }
    }

    private fun getLatestData() {
        _screenState.value = _screenState.value.copy(isLoading = true, isError = false)
        viewModelScope.launch {
            _screenState.value = _screenState.value.copy(
                isLoading = false,
                isError = !personalAnimeListUseCase.refreshPersonalAnimeStatuses(),
            )
        }
    }

    override fun onTabClicked(tab: Int) {
        _screenState.value = _screenState.value.copy(currentTab = tab)
    }

    override fun onReloadClicked() {
        startObserveMyListChanges()
    }

    override fun onResetStateClicked() {
        _screenState.value = _screenState.value.copy(isLoading = false, isError = false)
    }

    override fun onSwipeRefresh() {
        getLatestData()
    }

    override fun onSearchValueChanged(searchValue: String) {
        if (searchValue != screenState.value.searchValue) {
            _screenState.value = _screenState.value.copy(searchValue = searchValue)
            onListFiltered()
        }
    }

    override fun onSortTypeChanged(sortBy: SortType) {
        _screenState.update {
            it.copy(sortBy = sortBy)
        }
        viewModelScope.launch {
            settingsUseCase.updatePersonalListSort(sortBy)
        }
    }

    override fun onListFiltered() {
        viewModelScope.launch {
            _effectStream.emit(MyListScreenContract.Effect.ListWasFiltered)
        }
    }
}
