package com.kis.youranimelist.ui.explore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.model.AnimeCategory
import com.kis.youranimelist.model.ranking_response.AnimeRanked
import com.kis.youranimelist.repository.RepositoryNetwork
import java.util.function.Supplier
import java.util.stream.Collectors

class ExploreViewModel(private val liveDataToObserve: MutableLiveData<ExploreState> = MutableLiveData()) :
    ViewModel() {

    private val limit = 20

    fun getLiveData() = liveDataToObserve

    fun getAnimeListByGroup() {
        liveDataToObserve.value = ExploreState.Loading
        Thread {

            val listByCategory = mutableListOf<AnimeCategory>()
            try {
                val topAnimeAll: List<AnimeRanked> =
                    RepositoryNetwork.getAnimeRankingList("all", limit, null, null)
                val topAnimeAiring: List<AnimeRanked> =
                    RepositoryNetwork.getAnimeRankingList("airing", limit, null, null)
                val topAnimeMovies: List<AnimeRanked> =
                    RepositoryNetwork.getAnimeRankingList("movie", limit, null, null)
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