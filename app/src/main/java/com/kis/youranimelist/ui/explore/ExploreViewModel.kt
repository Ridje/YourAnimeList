package com.kis.youranimelist.ui.explore

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.R
import com.kis.youranimelist.model.app.Anime
import com.kis.youranimelist.model.app.AnimeCategory
import com.kis.youranimelist.repository.RepositoryNetwork
import com.kis.youranimelist.utils.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.stream.Collectors
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(private val repositoryNetwork: RepositoryNetwork,
                                           @ApplicationContext val context: Context) : ViewModel() {

    private val limit = 20
    private val liveDataToObserve: MutableLiveData<ExploreState> = MutableLiveData()

    @Inject
    lateinit var appPreferences: AppPreferences

    val results = mutableListOf(
        AnimeCategory("Top ranked") { repositoryNetwork.getAnimeRankingList("all", limit, null, fields) },
        AnimeCategory("Airing") { repositoryNetwork.getAnimeRankingList("airing", limit, null, fields) },
        AnimeCategory("Popular") { repositoryNetwork.getAnimeRankingList("bypopularity", limit, null, fields) },
        AnimeCategory("Favorite") { repositoryNetwork.getAnimeRankingList("favorite", limit, null, fields) }
    )

    val handler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        liveDataToObserve.value = ExploreState.Error(exception)
    }

    fun getLiveData() = liveDataToObserve

    @Synchronized
    fun updateResults() {
        liveDataToObserve.value = ExploreState.LoadingResult(results)
    }

    fun getAnimeListByGroup() {
        liveDataToObserve.value = ExploreState.LoadingResult(results)

        val nsfwSettingValue = appPreferences.readString(AppPreferences.NSFW_SETTING_KEY)
        val nsfwValues = context.resources.getStringArray(R.array.nsfw_values)

        viewModelScope.launch(handler) {
            val categories = results

            for (i in categories.indices) {
                val result = withContext(Dispatchers.IO) {
                    val requestResult = categories[i].networkGetter.invoke()
                    return@withContext requestResult.stream().filter { allowedContent(nsfwValues, nsfwSettingValue, it.anime.nsfw)}.map { Anime(it.anime) }.collect(Collectors.toList())
                }
                categories[i].animeList = result
                updateResults()
            }

        }
    }

    private fun allowedContent(nsfwValues: Array<String>, userValue: String, itemValue: String?): Boolean {
        return itemValue == null || userValue.isEmpty() || nsfwValues.indexOf(itemValue) <= nsfwValues.indexOf(userValue)
    }

    companion object {
        const val fields = "id, title, main_picture, nsfw"
    }
}