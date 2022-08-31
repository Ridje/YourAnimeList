package com.kis.youranimelist.ui.itembottomsheetdialog

import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatusValue
import com.kis.youranimelist.domain.rankinglist.model.Anime
import java.time.LocalDate

object ItemBottomScreenContract {
    data class ScreenState(
        val id: Int,
        val title: String,
        val statuses: List<String> = AnimeStatusValue.listOfIndiciesOnlyValues(),
        val currentStatus: Int = AnimeStatusValue.defaultStatusIndex(),
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
        object DataSaved : Effect()
        object DataSaveError : Effect()
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

fun ItemBottomScreenContract.ScreenState.copyWithMapping(animeStatus: AnimeStatus): ItemBottomScreenContract.ScreenState {
    return this.copy(
        id = animeStatus.anime.id,
        title = animeStatus.anime.title,
        currentStatus = if (this.statusModified) {
            this.currentStatus
        } else {
            AnimeStatusValue.listOfIndiciesOnlyValues()
                .indexOf(animeStatus.status.presentIndex)
        },
        episodesWatched = if (this.episodesWatchedModified) {
           this.episodesWatched
        } else {
            animeStatus.numWatchedEpisodes
        },
        episodes = animeStatus.anime.numEpisodes ?: 0,
        score = if (this.scoreModified) {
            this.score
        } else {
            animeStatus.score.toFloat()
        }
    )
}

fun ItemBottomScreenContract.ScreenState.asAnimeStatus(): AnimeStatus {
    return AnimeStatus(
        anime = Anime(
            id = this.id,
            title = this.title,
        ),
        status = AnimeStatusValue.Companion.Factory.getAnimeStatusByValue(
            this.statuses[this.currentStatus]),
        score = this.score.toInt(),
        numWatchedEpisodes = this.episodesWatched ?: 0,
        updatedAt = System.currentTimeMillis(),
    )
}

