package com.kis.youranimelist.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.data.repository.anime.AnimeRepository
import com.kis.youranimelist.ui.navigation.InvalidNavArgumentException
import com.kis.youranimelist.ui.navigation.NavigationKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animeRepository: AnimeRepository,
) : ViewModel() {

    val animeId = savedStateHandle.get<Int>(NavigationKeys.Argument.ANIME_ID)
        ?: throw InvalidNavArgumentException(NavigationKeys.Argument.ANIME_ID)

    val screenState: MutableStateFlow<ItemScreenContract.ScreenState> = MutableStateFlow(
        ItemScreenContract.ScreenState(
            item = ItemScreenMapper.map(null)
        )
    )

    init {
        getAnimeInfo()
        getLatestData()
    }

    private fun getLatestData() {
        viewModelScope.launch() {
            animeRepository.refreshAnimeDetailedData(animeID = animeId)
        }
    }

    private fun getAnimeInfo() {
        viewModelScope.launch {
            animeRepository.getAnimeDetailedDataSource(
                animeId,
            ).collect { anime ->
                screenState.value = ItemScreenContract.ScreenState(
                    item = ItemScreenMapper.map(anime)
                )
            }
        }
    }
}
