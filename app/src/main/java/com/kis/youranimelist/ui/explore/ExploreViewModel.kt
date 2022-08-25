package com.kis.youranimelist.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.data.repository.anime.AnimeRepository
import com.kis.youranimelist.domain.model.ResultWrapper
import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.domain.rankinglist.model.AnimeCategory
import com.kis.youranimelist.ui.model.AnimeCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
) : ViewModel() {

    private val _screenState: MutableStateFlow<ExploreScreenContract.ScreenState> =
        MutableStateFlow(
            ExploreScreenContract.ScreenState(
                AnimeCategories.animeCategories,
                buildList(AnimeCategories.animeCategories.size) { addAll(AnimeCategories.animeCategories.map { false }) },
            )
        )

    val screenState: StateFlow<ExploreScreenContract.ScreenState>
        get() {
            return _screenState
        }

    private val limit = 20

    init {
        getAnimeListByGroup()
    }

    private fun getAnimeListByGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            val jobsList = mutableListOf<Deferred<ResultWrapper<List<Anime>>>>()
            for (i in AnimeCategories.animeCategories.indices) {
                jobsList.add(async(Dispatchers.IO) {
                    return@async animeRepository.getRankingAnimeList(
                        AnimeCategories.animeCategories[i].tag,
                        limit,
                        null,
                    )
                })

            }

            for (i in jobsList.indices) {
                val result = jobsList[i].await()
                _screenState.value = when (result) {
                    is ResultWrapper.Success -> {
                        val currentList = _screenState.value.categories.toMutableList()
                        currentList[i] = AnimeCategory(AnimeCategories.animeCategories[i].name,
                            AnimeCategories.animeCategories[i].tag,
                            result.data)
                        ExploreScreenContract.ScreenState(
                            currentList,
                            _screenState.value.listErrors.mapIndexed { index, b -> if (index == i) false else b }
                        )
                    }
                    is ResultWrapper.Error -> _screenState.value.copy(listErrors = _screenState.value.listErrors.mapIndexed { index, b -> if (index == i) true else b })
                    ResultWrapper.Loading -> {
                        _screenState.value
                    }
                }
            }
        }
    }

    private fun allowedContent(
        nsfwValues: Array<String>,
        userValue: String,
        itemValue: String?,
    ): Boolean {
        return itemValue == null || userValue.isEmpty() || nsfwValues.indexOf(itemValue) <= nsfwValues.indexOf(
            userValue)
    }
}
