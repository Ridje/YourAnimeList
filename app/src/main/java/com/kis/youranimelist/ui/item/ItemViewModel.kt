package com.kis.youranimelist.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.data.repository.anime.AnimeRepository
import com.kis.youranimelist.ui.navigation.InvalidNavArgumentException
import com.kis.youranimelist.ui.navigation.NavigationKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animeRepository: AnimeRepository,
) : ViewModel() {

    private val animeId = savedStateHandle.get<Int>(NavigationKeys.Argument.ANIME_ID)
        ?: throw InvalidNavArgumentException(NavigationKeys.Argument.ANIME_ID)

    private val _screenState: MutableStateFlow<ItemScreenContract.ScreenState> = MutableStateFlow(
        ItemScreenContract.ScreenState(
            item = defaultAnimeItem
        )
    )
    val screenState = _screenState as StateFlow<ItemScreenContract.ScreenState>

    init {
        getAnimeInfo()
        getLatestData()
    }

    private fun getLatestData() {
        viewModelScope.launch {
            animeRepository.refreshAnimeDetailedData(animeID = animeId)
        }
    }

    private fun getAnimeInfo() {
        viewModelScope.launch {
            animeRepository.getAnimeDetailedDataSource(
                animeId,
            ).collect { anime ->
                _screenState.value = ItemScreenContract.ScreenState(
                    item = anime.asAnimeItemScreen()
                )
            }
        }
    }
}
