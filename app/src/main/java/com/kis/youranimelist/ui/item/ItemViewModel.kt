package com.kis.youranimelist.ui.item

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.repository.RepositoryMock

class ItemViewModel(private val liveDataToObserve: MutableLiveData<ItemState> = MutableLiveData()) : ViewModel() {
    fun getLiveData() = liveDataToObserve

    fun getAnimeInfo(anime : Anime) {
        liveDataToObserve.value = ItemState.Loading
        Thread {
            Thread.sleep(1000)
            liveDataToObserve.postValue(ItemState.Success(RepositoryMock.getAnimeInfo(anime)))
        }.start()
    }
}