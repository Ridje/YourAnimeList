package com.kis.youranimelist.ui.explore

import com.kis.youranimelist.model.app.AnimeCategory

sealed class ExploreState {
    data class LoadingResult (val animeData: MutableList<AnimeCategory>) : ExploreState()
    data class Error (val error:Throwable) : ExploreState()
}