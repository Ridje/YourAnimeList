package com.kis.youranimelist.ui.explore

import com.kis.youranimelist.domain.rankinglist.model.AnimeCategory

object ExploreScreenContract {

    data class ScreenState(
        val categories: List<AnimeCategory>,
    )

    sealed class Effect {}

    interface EventsConsumer {
    }
}
