package com.kis.youranimelist.ui.explore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.model.app.AnimeCategory
import com.kis.youranimelist.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
) : ViewModel() {

    val screenState: MutableStateFlow<ExploreScreenContract.ScreenState> = MutableStateFlow(
        ExploreScreenContract.ScreenState(
            listOf()
        )
    )

    val effectStream: MutableSharedFlow<ExploreScreenContract.Effect> = MutableSharedFlow()

    private val limit = 20
    private val requests = mutableListOf(
        AnimeCategory("Top ranked", "all", listOf()),
        AnimeCategory("Airing", "airing", listOf()),
        AnimeCategory("Popular", "bypopularity", listOf()),
//        AnimeCategory("Favorite", "favourite")
    )

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
                currentList.add(AnimeCategory(requests[i].name, requests[i].tag, result))
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
