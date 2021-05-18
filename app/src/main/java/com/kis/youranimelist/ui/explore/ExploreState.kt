package com.kis.youranimelist.ui.explore

import com.kis.youranimelist.model.AnimeCategory

sealed class ExploreState {
    data class Success (val animeData:List<AnimeCategory>) : ExploreState()
    data class Error (val error:Throwable) : ExploreState()
    object Loading : ExploreState()
}
