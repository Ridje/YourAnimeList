package com.kis.youranimelist.ui.explore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.model.app.Anime
import com.kis.youranimelist.model.app.AnimeCategory
import com.kis.youranimelist.repository.RepositoryNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.stream.Collectors
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(private val repositoryNetwork: RepositoryNetwork) : ViewModel() {


    private val limit = 20
    private val liveDataToObserve: MutableLiveData<ExploreState> = MutableLiveData()

    val results = mutableListOf(
        AnimeCategory("Top ranked") { repositoryNetwork.getAnimeRankingList("all", limit, null, null) },
        AnimeCategory("Airing") { repositoryNetwork.getAnimeRankingList("airing", limit, null, null) },
        AnimeCategory("Popular") { repositoryNetwork.getAnimeRankingList("bypopularity", limit, null, null) },
        AnimeCategory("Favorite") { repositoryNetwork.getAnimeRankingList("favorite", limit, null, null) }
    )

    val handler = CoroutineExceptionHandler { _, exception ->
        liveDataToObserve.value = ExploreState.Error(exception)
    }

    fun getLiveData() = liveDataToObserve

    @Synchronized
    fun updateResults() {
        liveDataToObserve.value = ExploreState.LoadingResult(results)
    }

    fun getAnimeListByGroup() {
        liveDataToObserve.value = ExploreState.LoadingResult(results)
        viewModelScope.launch(handler) {
            val categories = results

            for (i in categories.indices) {
                val result = withContext(Dispatchers.IO) {
                    val requestResult = categories[i].networkGetter.invoke()
                    return@withContext requestResult.stream().map { Anime(it.anime) }.collect(Collectors.toList())
                }
                categories[i].animeList = result
                updateResults()
            }

        }
    }
}