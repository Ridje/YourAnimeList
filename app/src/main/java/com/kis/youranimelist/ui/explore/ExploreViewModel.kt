package com.kis.youranimelist.ui.explore

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.R
import com.kis.youranimelist.model.app.Anime
import com.kis.youranimelist.model.app.AnimeCategory
import com.kis.youranimelist.repository.RepositoryNetwork
import com.kis.youranimelist.utils.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repositoryNetwork: RepositoryNetwork,
    @ApplicationContext val context: Context,
) : ViewModel() {

    val screenState: MutableStateFlow<ExploreScreenContract.ScreenState> = MutableStateFlow(
        ExploreScreenContract.ScreenState(
            listOf()
        )
    )

    val effectStream: MutableSharedFlow<ExploreScreenContract.Effect> = MutableSharedFlow()

    private val limit = 20

    val results = mutableListOf(
        AnimeCategory("Top ranked", "all"),
        AnimeCategory("Airing", "airing"),
        AnimeCategory("Popular", "bypopularity"),
        AnimeCategory("Favorite", "favourite")
    )

    init {
        getAnimeListByGroup()
    }

    fun getAnimeListByGroup() {
        viewModelScope.launch {
            for (i in results.indices) {
                val result = withContext(Dispatchers.IO) {
                    val requestResult =
                        repositoryNetwork.getAnimeRankingList(results[i].tag, limit, null, fields)
                    return@withContext requestResult.map { Anime(it.anime) }
                }
                results[i] = results[i].copy(animeList = result)
            }

            screenState.value = screenState.value.copy(categories = results)
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
        const val fields = "id, title, main_picture, nsfw"
    }
}
