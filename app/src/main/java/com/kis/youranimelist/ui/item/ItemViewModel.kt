package com.kis.youranimelist.ui.item

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.repository.RepositoryMock
import com.kis.youranimelist.repository.RepositoryNetwork

class ItemViewModel(private val liveDataToObserve: MutableLiveData<ItemState> = MutableLiveData()) : ViewModel() {
    fun getLiveData() = liveDataToObserve

    fun getAnimeInfo(anime : Anime) {
        liveDataToObserve.value = ItemState.Loading
        Thread {
            Thread.sleep(1000)
            try {
                liveDataToObserve.postValue(
                    ItemState.Success(
                        RepositoryNetwork.getAnimeInfo(
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