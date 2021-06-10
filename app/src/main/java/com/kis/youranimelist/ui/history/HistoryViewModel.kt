package com.kis.youranimelist.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.model.app.AnimeViewHistory
import com.kis.youranimelist.repository.RepositoryLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(val repositoryLocal: RepositoryLocal) : ViewModel() {


    private val liveDataToObserve = MutableLiveData<HistoryState>()
    fun getLiveData() = liveDataToObserve

    init {
        getViewHistory()
    }

    fun getViewHistory() {
        liveDataToObserve.value = HistoryState.Loading
        try {
            viewModelScope.launch(Dispatchers.IO) {
                liveDataToObserve.postValue(
                    HistoryState.Success(
                        repositoryLocal.getAnimeViewHistory().stream().map { a -> AnimeViewHistory(a.animeID, a.animeName, a.createdAt) }.collect(Collectors.toList()))
                )
            }
        }
        catch (exception: Throwable) {
            liveDataToObserve.value = HistoryState.Error(exception)
        }
    }

    companion object {
        const val fields = "id, title, main_picture, nsfw"
    }
}