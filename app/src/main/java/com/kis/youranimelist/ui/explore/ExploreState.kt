package com.kis.youranimelist.ui.explore

import com.kis.youranimelist.model.Anime

sealed class ExploreState {
    data class Success (val animeData:List<Map<String, Any>>) : ExploreState()
    data class Error (val error:Throwable) : ExploreState()
    object Loading : ExploreState()
}
