package com.kis.youranimelist.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.domain.rankinglist.model.AnimeCategory
import com.kis.youranimelist.data.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
) : ViewModel() {

    private val requests = mutableListOf(
        AnimeCategory("Top ranked", "all", listOf(
            null,
            null,
            null,
            null,
        )),
        AnimeCategory("Airing", "airing", listOf(
            null,
            null,
            null,
            null,
        )),
        AnimeCategory("Popular", "bypopularity", listOf(
            null,
            null,
            null,
            null
        )),
        AnimeCategory("Upcoming", "upcoming", listOf(
            null,
            null,
            null,
            null
        )),
        AnimeCategory("Movies", "movie", listOf(
            null,
            null,
            null,
            null
        )),
        AnimeCategory("Favorite", "favorite", listOf(
            null,
            null,
            null,
            null
        )),
    )
    val screenState: MutableStateFlow<ExploreScreenContract.ScreenState> = MutableStateFlow(
        ExploreScreenContract.ScreenState(
            requests
        )
    )

    private val limit = 20

    init {
        getAnimeListByGroup()
    }

    private fun getAnimeListByGroup() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
        }
        ) {
            for (i in requests.indices) {
                val result = withContext(Dispatchers.IO) {
                    return@withContext animeRepository.getRankingAnimeList(requests[i].tag,
                        limit,
                        null,
                        fields)
                }
                val currentList = screenState.value.categories.toMutableList()
                currentList[i] = AnimeCategory(requests[i].name, requests[i].tag, result)
                screenState.value = ExploreScreenContract.ScreenState(
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

    companion object {
        const val fields = "id, title, main_picture, start_season"
    }
}
