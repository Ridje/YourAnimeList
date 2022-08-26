package com.kis.youranimelist.ui.search

object SearchScreenContract {

    data class ScreenState(
        val searchValue: String,
    )

    interface ScreenEventsListener {
        fun onSearchClick(searchValue: String)
        fun onSearchValueChanged(searchValue: String)
    }
}
