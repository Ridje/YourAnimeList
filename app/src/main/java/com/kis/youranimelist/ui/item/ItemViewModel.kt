package com.kis.youranimelist.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.domain.anime.AnimeDetailedUseCase
import com.kis.youranimelist.domain.rankinglist.model.RelatedAnime
import com.kis.youranimelist.ui.navigation.InvalidNavArgumentException
import com.kis.youranimelist.ui.navigation.NavigationKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    animeDetailedUseCase: AnimeDetailedUseCase,
) : ViewModel() {

    private val animeId = savedStateHandle.get<Int>(NavigationKeys.Argument.ANIME_ID)
        ?: throw InvalidNavArgumentException(NavigationKeys.Argument.ANIME_ID)

    val screenState: StateFlow<ItemScreenContract.ScreenState> =
        animeDetailedUseCase.getAnimeDetailedProducer(animeId)
            .map { anime -> ItemScreenContract.ScreenState(anime.asAnimeItemScreen(RelatedAnime::asRelatedAnimeItemScreen)) }
            .stateIn(viewModelScope,
                SharingStarted.Lazily,
                initialValue = ItemScreenContract.ScreenState(item = defaultAnimeItem))

    init {
        viewModelScope.launch {
            animeDetailedUseCase.refreshAnimeDetailedData(animeId)
        }
    }
}
