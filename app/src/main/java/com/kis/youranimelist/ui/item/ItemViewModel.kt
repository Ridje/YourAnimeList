package com.kis.youranimelist.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.NavigationKeys
import com.kis.youranimelist.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animeRepository: AnimeRepository,
) : ViewModel() {

    val screenState: MutableStateFlow<ItemScreenContract.ScreenState> = MutableStateFlow(
        ItemScreenContract.ScreenState(
            item = ItemScreenMapper.map(null)
        )
    )

    init {
        getAnimeInfo(
            savedStateHandle.get<Int>(NavigationKeys.Argument.ANIME_ID)
                ?: throw RuntimeException("WTF")
        )
    }

    private fun getAnimeInfo(animeID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            animeRepository.getAnimeDetailedData(
                animeID,
                fields
            ).collect { anime ->
                screenState.value = ItemScreenContract.ScreenState(
                    item = ItemScreenMapper.map(anime)
                )
            }
        }
    }

    companion object {
        const val fields =
            "id, title, mean, main_picture, start_season, synopsis, genres, pictures, related_anime"
    }
}
