package com.kis.youranimelist.ui.mylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.domain.Result
import com.kis.youranimelist.domain.personalanimelist.PersonalAnimeListUseCase
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyListViewModel @Inject constructor(
    personalAnimeListUseCase: PersonalAnimeListUseCase,
) : ViewModel(), MyListScreenContract.ScreenEventsListener {

    private val animeStatusesPageSource: Flow<Result<List<AnimeStatus>>> =
        personalAnimeListUseCase.getPersonalAnimeListProducer()

    private val _screenState: MutableStateFlow<MyListScreenContract.ScreenState> = MutableStateFlow(
        MyListScreenContract.ScreenState(isLoading = true, items = listOf()))
    val screenState: StateFlow<MyListScreenContract.ScreenState>
        get() = _screenState

    init {
        startObserveMyListChanges()
    }

    private fun startObserveMyListChanges() {
        viewModelScope.launch {
            _screenState.value = _screenState.value.copy(
                isLoading = true,
                isError = false,
            )
            animeStatusesPageSource
                .flowOn(Dispatchers.IO)
                .collectLatest { result ->
                    val newValue = when (result) {
                        is Result.Success -> {
                            _screenState.value.copy(
                                isLoading = false,
                                isError = false,
                                items = result.data.map {
                                    MyListScreenContract.Item(
                                        it.anime.id,
                                        it.status.presentIndex,
                                        it.anime.title,
                                        it.status.color,
                                        it.anime.picture?.large,
                                        it.anime.mediaType,
                                        it.numWatchedEpisodes,
                                        it.anime.numEpisodes,
                                        it.score,
                                        it.anime.mean,
                                    )
                                }.sortedWith(compareBy({ it.status }, { it.title })))
                        }
                        is Result.Error -> {
                            _screenState.value.copy(
                                isLoading = false,
                                isError = true,
                            )
                        }
                        Result.Loading -> {
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

    override fun onTabClicked(tab: Int) {
        _screenState.value = _screenState.value.copy(currentTab = tab)
    }

    override fun onReloadClicked() {
        startObserveMyListChanges()
    }

    override fun onResetStateClicked() {
        _screenState.value = _screenState.value.copy(isLoading = false, isError = false)
    }
}
