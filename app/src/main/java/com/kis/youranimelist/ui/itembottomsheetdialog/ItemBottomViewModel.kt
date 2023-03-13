package com.kis.youranimelist.ui.itembottomsheetdialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.R
import com.kis.youranimelist.core.ResourceProvider
import com.kis.youranimelist.domain.model.ResultWrapper
import com.kis.youranimelist.domain.personalanimelist.PersonalAnimeListUseCase
import com.kis.youranimelist.ui.navigation.InvalidNavArgumentException
import com.kis.youranimelist.ui.navigation.NavigationKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemBottomViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val personalAnimeListUseCase: PersonalAnimeListUseCase,
    resourceProvider: ResourceProvider,
) : ViewModel(), ItemBottomScreenContract.ScreenEventsListener {

    private val _screenState: MutableStateFlow<ItemBottomScreenContract.ScreenState>
    val screenState: StateFlow<ItemBottomScreenContract.ScreenState>
        get() = _screenState

    private val _effectStream: MutableSharedFlow<ItemBottomScreenContract.Effect> =
        MutableSharedFlow()
    val effectStream: SharedFlow<ItemBottomScreenContract.Effect>
        get() = _effectStream

    private val id = savedStateHandle.get<Int>(NavigationKeys.Argument.ANIME_ID)
        ?: throw InvalidNavArgumentException(NavigationKeys.Argument.ANIME_ID)

    init {
        _screenState =
            MutableStateFlow(
                ItemBottomScreenContract.ScreenState(
                    id = id, title = resourceProvider.getString(
                        R.string.loading
                    ), comments = null,
                )
            )
    }

    init {
        startObserveMyItemChanges()
    }

    private fun startObserveMyItemChanges() {
        viewModelScope.launch {
            personalAnimeListUseCase.getPersonalAnimeStatusProducer(id)
                .collectLatest { result ->
                    when (result) {
                        is ResultWrapper.Success -> _screenState.value =
                            screenState.value.copyWithMapping(result.data)
                        else -> {}
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
            when (personalAnimeListUseCase.savePersonalAnimeStatus(screenState.value.asAnimeStatus())) {
                true -> {
                    _effectStream.emit(ItemBottomScreenContract.Effect.DataSaved)
                }
                false -> {
                    _screenState.value = screenState.value.copy(
                        applyLoading = false
                    )
                }
            }
        }
    }

    override fun onDeleteEntryClick() {
        _screenState.value = screenState.value.copy(
            deleteLoading = true,
        )
        viewModelScope.launch {
            when (personalAnimeListUseCase.deletePersonalAnimeStatus(screenState.value.id)) {
                true -> {
                    _effectStream.emit(ItemBottomScreenContract.Effect.DataSaved)
                }
                false -> {
                    _screenState.value = screenState.value.copy(
                        deleteLoading = false
                    )
                }
            }
        }
    }
}
