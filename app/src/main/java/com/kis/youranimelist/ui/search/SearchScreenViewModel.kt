package com.kis.youranimelist.ui.search

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(): ViewModel(), SearchScreenContract.ScreenEventsListener {

    private val _screenState = MutableStateFlow(
        SearchScreenContract.ScreenState(
            ""
        )
    )

    val screenState = _screenState as StateFlow<SearchScreenContract.ScreenState>


    override fun onSearchClick(searchValue: String) {

    }

    override fun onSearchValueChanged(searchValue: String) {
        _screenState.value = _screenState.value.copy(
            searchValue = searchValue
        )
    }
}
