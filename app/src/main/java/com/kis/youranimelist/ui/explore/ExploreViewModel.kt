package com.kis.youranimelist.ui.explore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.model.AnimeCategory
import com.kis.youranimelist.model.ranking_response.AnimeRanked
import com.kis.youranimelist.repository.RepositoryNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.stream.Collectors
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(private val repositoryNetwork: RepositoryNetwork) : ViewModel() {

    private val limit = 20
    private val liveDataToObserve: MutableLiveData<ExploreState> = MutableLiveData()

    fun getLiveData() = liveDataToObserve

    fun getAnimeListByGroup() {
        liveDataToObserve.value = ExploreState.Loading
        Thread {

            val listByCategory = mutableListOf<AnimeCategory>()
            try {
                val topAnimeAll: List<AnimeRanked> =
                    repositoryNetwork.getAnimeRankingList("all", limit, null, null)
                val topAnimeAiring: List<AnimeRanked> =
                    repositoryNetwork.getAnimeRankingList("airing", limit, null, null)
                val topAnimeMovies: List<AnimeRanked> =
                    repositoryNetwork.getAnimeRankingList("movie", limit, null, null)
                listByCategory.add(
                    AnimeCategory(
                        "Top ranked",
                        topAnimeAll.stream().map { Anime(it.anime) }.collect(Collectors.toList())
                    )
                )
                listByCategory.add(
                    AnimeCategory(
                        "Airing",
                        topAnimeAiring.stream().map { Anime(it.anime) }.collect(Collectors.toList())
                    )
                )
                listByCategory.add(
                    AnimeCategory(
                        "Movie",
                        topAnimeMovies.stream().map { Anime(it.anime) }.collect(Collectors.toList())
                    )
                )
            } catch (e: Exception) {
                liveDataToObserve.postValue(ExploreState.Error(e))
                e.printStackTrace()
            }
            liveDataToObserve.postValue(ExploreState.Success(listByCategory))
        }.start()
    }
}