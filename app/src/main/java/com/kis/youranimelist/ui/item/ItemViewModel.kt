package com.kis.youranimelist.ui.item

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.repository.RepositoryMock
import com.kis.youranimelist.repository.RepositoryNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(val repositoryNetwork: RepositoryNetwork) : ViewModel() {

    private val liveDataToObserve = MutableLiveData<ItemState>()
    fun getLiveData() = liveDataToObserve

    fun getAnimeInfo(anime : Anime) {
        liveDataToObserve.value = ItemState.Loading
        Thread {
            Thread.sleep(1000)
            try {
                liveDataToObserve.postValue(
                    ItemState.Success(
                        repositoryNetwork.getAnimeInfo(
                            anime.id,
                            fields
                        )
                    )
                )
            } catch (e : Exception) {
                liveDataToObserve.postValue(ItemState.Error(e))
                e.printStackTrace()
            }
        }.start()
    }
    companion object {
        const val fields = "id, title, mean, main_picture, start_season, synopsis"
    }
}