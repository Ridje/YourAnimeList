package com.kis.youranimelist.ui.itembottomsheetdialog

import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatusValue

object ItemBottomScreenContract {
    data class ScreenState(
        val id: Int,
        val title: String,
        val statuses: List<String> = AnimeStatusValue.listOfIndiciesOnlyValues(),
        val currentStatus: Int = 0,
        val episodesWatched: Int? = null,
        val episodes: Int = 0,
        val score: Float = 0f,
        val episodesWatchedModified: Boolean = false,
        val scoreModified: Boolean = false,
        val statusModified: Boolean = false,
        val applyLoading: Boolean = false,
        val deleteLoading: Boolean = false,
    )

    sealed class Effect {
        object DataSaved: Effect()
        object DataSaveError: Effect()
    }

    interface ScreenEventsListener {
        fun onScoreChanged(score: Float)
        fun onEpisodesWatchedChanged(value: Int?)
        fun onAdditionOneEpisodeWatched()
        fun onSubtractionOneEpisodeWatched()
        fun onStatusChanged(status: String)
        fun onApplyChanges()
        fun onDeleteEntryClick()
    }
}
