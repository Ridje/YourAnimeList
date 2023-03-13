package com.kis.youranimelist.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.core.ResourceProvider
import com.kis.youranimelist.domain.anime.AnimeDetailedUseCase
import com.kis.youranimelist.domain.personalanimelist.PersonalAnimeListUseCase
import com.kis.youranimelist.ui.navigation.InvalidNavArgumentException
import com.kis.youranimelist.ui.navigation.NavigationKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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
    resourceProvider: ResourceProvider,
    private val personalAnimeListUseCase: PersonalAnimeListUseCase,
) : ViewModel(), ItemScreenContract.ScreenEventsListener {

    private val animeId = savedStateHandle.get<Int>(NavigationKeys.Argument.ANIME_ID)
        ?: throw InvalidNavArgumentException(NavigationKeys.Argument.ANIME_ID)

    private val _effectStream: MutableSharedFlow<ItemScreenContract.Effect> =
        MutableSharedFlow()
    val effectStream: SharedFlow<ItemScreenContract.Effect>
        get() = _effectStream

    val screenState: StateFlow<ItemScreenContract.ScreenState> =
        animeDetailedUseCase.getAnimeDetailedProducer(animeId)
            .map { anime ->
                ItemScreenContract.ScreenState(
                    item = anime.asAnimeItemScreen(resourceProvider),
                    listRelatedItems = anime.relatedAnime.map { it.asRelatedAnimeItemScreen() },
                    listRecommendedItems = anime.recommendedAnime
                        .map { it.asRecommendedAnimeItemScreen() }
                        .sortedByDescending { it.recommendedTimes }
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                initialValue = ItemScreenContract.ScreenState(
                    item = ItemScreenContract.AnimeItem.Factory(resourceProvider).defaultAnimeItem,
                    listOf(),
                    listOf(),
                )
            )

    init {
        viewModelScope.launch {
            animeDetailedUseCase.refreshAnimeDetailedData(animeId)
        }
    }

    override fun onAddToBookmarksButtonPressed() {
        viewModelScope.launch {
            if (personalAnimeListUseCase.savePersonalAnimeStatus(screenState.value.item.asAnimeStatus())) {
                _effectStream.emit(ItemScreenContract.Effect.ItemAddedToList)
            }
        }
    }
}
