package com.kis.youranimelist.ui.explore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kis.youranimelist.repository.Repository
import com.kis.youranimelist.repository.RepositoryMock
import java.lang.Thread.sleep

class ExploreViewModel(private val liveDataToObserve: MutableLiveData<ExploreState> = MutableLiveData()) :
    ViewModel() {

    private val repository : Repository = RepositoryMock()

    fun getLiveData() = liveDataToObserve

    fun getAnimeListByGroup() {
        liveDataToObserve.value = ExploreState.Loading
        Thread {
            sleep(1000)
            liveDataToObserve.postValue(ExploreState.Success(repository.getAnimeListByGroup()))
        }.start()
    }
}