package com.kis.youranimelist.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.data.repository.anime.AnimeRepository
import com.kis.youranimelist.domain.rankinglist.model.AnimeCategory
import com.kis.youranimelist.ui.model.AnimeCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
) : ViewModel() {

    private val _screenState: MutableStateFlow<ExploreScreenContract.ScreenState> = MutableStateFlow(
        ExploreScreenContract.ScreenState(
            AnimeCategories.animeCategories
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
            for (i in AnimeCategories.animeCategories.indices) {
                val result = withContext(Dispatchers.IO) {
                    return@withContext animeRepository.getRankingAnimeList(
                        AnimeCategories.animeCategories[i].tag,
                        limit,
                        null,
                    )
                }
                val currentList = _screenState.value.categories.toMutableList()
                currentList[i] = AnimeCategory(AnimeCategories.animeCategories[i].name,
                    AnimeCategories.animeCategories[i].tag,
                    result)
                _screenState.value = ExploreScreenContract.ScreenState(
                    currentList
                )
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
