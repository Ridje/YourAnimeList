package com.kis.youranimelist.ui.explore

sealed class ExploreState {
    data class Success (val animeData:Any) : ExploreState()
    data class Error (val error:Throwable) : ExploreState()
    object Loading : ExploreState()
}
