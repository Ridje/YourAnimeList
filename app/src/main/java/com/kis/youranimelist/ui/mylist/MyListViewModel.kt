package com.kis.youranimelist.ui.mylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.domain.personalanimelist.PersonalAnimeListUseCase
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyListViewModel @Inject constructor(
    private val personalAnimeListUseCase: PersonalAnimeListUseCase,
) : ViewModel() {

    private val animeStatusesPageSource: Flow<List<AnimeStatus>> =
        personalAnimeListUseCase.getPersonalAnimeListProducer()

    val screenState: MutableStateFlow<MyListScreenContract.ScreenState> = MutableStateFlow(
        MyListScreenContract.ScreenState(true, listOf()))

    init {
        startObserveMyListChanges()
    }

    private fun startObserveMyListChanges() {
        viewModelScope.launch {
            animeStatusesPageSource
                .flowOn(Dispatchers.IO)
                .collectLatest { anime ->
                    val newValue = screenState.value.copy(
                        isLoading = false,
                        items = anime.map {
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
                        }.sortedWith(compareBy({ it.status }, { it.title }))
                    )

                    screenState.value = newValue
                }
        }
    }

    fun onTabClicked(tab: Int) {
        screenState.value = screenState.value.copy(currentTab = tab)
    }
}
