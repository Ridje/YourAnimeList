package com.kis.youranimelist.ui.itembottomsheetdialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.domain.Result
import com.kis.youranimelist.domain.personalanimelist.PersonalAnimeListUseCase
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatusValue
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.ui.navigation.InvalidNavArgumentException
import com.kis.youranimelist.ui.navigation.NavigationKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ItemBottomViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val personalAnimeListUseCase: PersonalAnimeListUseCase,
    private val dispatchers: Dispatchers,
) : ViewModel(), ItemBottomScreenContract.ScreenEventsListener {

    private val _screenState: MutableStateFlow<ItemBottomScreenContract.ScreenState>
    val screenState: StateFlow<ItemBottomScreenContract.ScreenState>
        get() = _screenState

    val effectStream: MutableSharedFlow<ItemBottomScreenContract.Effect> = MutableSharedFlow()

    private val id = savedStateHandle.get<Int>(NavigationKeys.Argument.ANIME_ID)
        ?: throw InvalidNavArgumentException(NavigationKeys.Argument.ANIME_ID)

    init {
        _screenState =
            MutableStateFlow(ItemBottomScreenContract.ScreenState(id = id, title = "Loading"))
    }

    init {
        startObserveMyItemChanges()
    }

    private fun startObserveMyItemChanges() {
        viewModelScope.launch {
            personalAnimeListUseCase.getPersonalAnimeStatusProducer(id)
                .flowOn(Dispatchers.IO)
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> _screenState.value = screenState.value.copy(
                            id = result.data.anime.id,
                            title = result.data.anime.title,
                            currentStatus = if (screenState.value.statusModified) {
                                screenState.value.currentStatus
                            } else {
                                AnimeStatusValue.listOfIndiciesOnlyValues()
                                    .indexOf(result.data.status.presentIndex)
                            },
                            episodesWatched = if (screenState.value.episodesWatchedModified) {
                                screenState.value.episodesWatched
                            } else {
                                result.data.numWatchedEpisodes
                            },
                            episodes = result.data.anime.numEpisodes ?: 0,
                            score = if (screenState.value.scoreModified) {
                                screenState.value.score
                            } else {
                                result.data.score.toFloat()
                            },
                        )
                        is Result.Error -> {}
                        else -> {
                        }
                    }
                }
        }
    }

    override fun onScoreChanged(score: Float) {
        _screenState.value = _screenState.value.copy(
            score = score,
            scoreModified = true,
        )
    }


    override fun onEpisodesWatchedChanged(value: Int?) {
        value?.let {
            if (value <= screenState.value.episodes) {
                _screenState.value = _screenState.value.copy(
                    episodesWatched = value,
                    episodesWatchedModified = true,
                )
            } else if (
                (value / 10) <= screenState.value.episodes) {
                _screenState.value = _screenState.value.copy(
                    episodesWatched = value / 10,
                    episodesWatchedModified = true
                )
            } else {
                _screenState.value = _screenState.value.copy(
                    episodesWatched = _screenState.value.episodes
                )
            }
        } ?: run {
            _screenState.value = _screenState.value.copy(
                episodesWatched = 0,
                episodesWatchedModified = true,
            )
        }
    }

    override fun onAdditionOneEpisodeWatched() {
        val newValue = screenState.value.episodesWatched?.let { currentValue ->
            currentValue + 1
        } ?: 1
        if (newValue <= screenState.value.episodes) {
            onEpisodesWatchedChanged(newValue)
        }
    }

    override fun onSubtractionOneEpisodeWatched() {
        val newValue = screenState.value.episodesWatched?.let { currentValue ->
            currentValue + -1
        } ?: 0
        if (newValue >= 0) {
            onEpisodesWatchedChanged(newValue)
        }
    }

    override fun onStatusChanged(status: String) {
        _screenState.value = screenState.value.copy(
            currentStatus = screenState.value.statuses.indexOf(status),
            statusModified = true,
        )
    }

    override fun onApplyChanges() {
        _screenState.value = screenState.value.copy(
            applyLoading = true,
        )
        viewModelScope.launch {
            val result = withContext(dispatchers.IO) {
                personalAnimeListUseCase.savePersonalAnimeStatus(
                    AnimeStatus(
                        anime = Anime(
                            id = screenState.value.id,
                            title = screenState.value.title,
                        ),
                        status = AnimeStatusValue.Companion.Factory.getAnimeStatusByValue(
                            screenState.value.statuses[screenState.value.currentStatus]),
                        score = screenState.value.score.toInt(),
                        numWatchedEpisodes = screenState.value.episodesWatched ?: 0,
                    )
                )
            }
            when (result) {
                true -> {
                    effectStream.emit(ItemBottomScreenContract.Effect.DataSaved)
                }
                false -> {
                    effectStream.emit(ItemBottomScreenContract.Effect.DataSaveError)
                }
            }
        }
    }
}
