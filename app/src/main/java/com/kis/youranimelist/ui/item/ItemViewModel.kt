package com.kis.youranimelist.ui.item

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kis.youranimelist.model.app.Anime
import com.kis.youranimelist.repository.RepositoryLocal
import com.kis.youranimelist.repository.RepositoryNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(val repositoryNetwork: RepositoryNetwork, val repositoryLocal: RepositoryLocal) : ViewModel() {

    private val liveDataToObserve = MutableLiveData<ItemState>()
    fun getLiveData() = liveDataToObserve

    fun getAnimeInfo(anime : Anime) {
        liveDataToObserve.value = ItemState.Loading
        Thread {
            try {
                repositoryLocal.addAnimeViewHistory(anime.id, anime.title)
                val anime = Anime(
                    repositoryNetwork.getAnimeInfo(
                        anime.id,
                        fields))
                anime.userNote = repositoryLocal.getUserNote(anime.id)?.userNote ?: ""
                liveDataToObserve.postValue(ItemState.Success(anime))
            } catch (e: Exception) {
                liveDataToObserve.postValue(ItemState.Error(e))
                e.printStackTrace()
            }
        }.start()
    }

    fun writeAnimeNote(anime: Anime) {
        Thread {
            repositoryLocal.writeUserNote(anime.id, anime.userNote)
        }.start()
    }

    companion object {
        const val fields = "id, title, mean, main_picture, start_season, synopsis"
    }
}