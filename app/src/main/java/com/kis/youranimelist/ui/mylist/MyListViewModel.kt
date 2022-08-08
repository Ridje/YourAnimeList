package com.kis.youranimelist.ui.mylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.domain.PersonalAnimeListUseCase
import com.kis.youranimelist.model.app.AnimeStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
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
                .stateIn(viewModelScope)
                .collectLatest { anime ->
                    screenState.value = MyListScreenContract.ScreenState(false, anime.map {
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
        }
    }

    fun onTabClicked(tab: Int) {
        screenState.value = screenState.value.copy(currentTab = tab)
    }
}
