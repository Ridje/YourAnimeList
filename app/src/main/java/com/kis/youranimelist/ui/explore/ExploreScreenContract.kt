package com.kis.youranimelist.ui.explore

import com.kis.youranimelist.model.app.AnimeCategory

class ExploreScreenContract {

    data class ScreenState(
        val categories: List<AnimeCategory>,
    )

    sealed class Effect {}

    interface EventsConsumer {
    }
}
