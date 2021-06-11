package com.kis.youranimelist.ui.history

import com.kis.youranimelist.model.app.AnimeViewHistory


sealed class HistoryState {
    data class Success(val history : List<AnimeViewHistory>) : HistoryState()
    data class Error(val error : Throwable) : HistoryState()
    object Loading : HistoryState()
}
