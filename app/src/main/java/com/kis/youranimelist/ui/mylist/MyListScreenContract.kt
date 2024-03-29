package com.kis.youranimelist.ui.mylist

import androidx.compose.ui.graphics.Color
import com.kis.youranimelist.core.utils.SortType
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatus
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatusValue

object MyListScreenContract {
    data class ScreenState(
        val isLoading: Boolean = false,
        val isSwipeToRefreshTurnedOn: Boolean = false,
        val isError: Boolean = false,
        val errorText: String? = null,
        val items: List<Item>,
        val tabs: List<String> = AnimeStatusValue.listOfIndices(),
        val currentTab: Int = 0,
        val searchValue: String = "",
        val sortBy: SortType = SortType.Title,
        val sorts: List<SortType> = SortType.values().toList(),
    )

    data class Item(
        val id: Int,
        val status: String?,
        val title: String?,
        val color: Color,
        val imageUrl: String?,
        val mediaType: String?,
        val finishedEpisodes: Int?,
        val totalNumOfEpisodes: Int?,
        val score: Int?,
        val mean: Float?,
        val tags: List<String>?,
        val comments: String?,
        val updatedAt: Long?,
    )


    interface ScreenEventsListener {
        fun onTabClicked(tab: Int)
        fun onReloadClicked()
        fun onResetStateClicked()
        fun onSwipeRefresh()
        fun onSearchValueChanged(searchValue: String)
        fun onSortTypeChanged(sortBy: SortType)
        fun onListFiltered()
    }

    sealed class Effect {
        object ListWasFiltered : Effect()
    }
}

fun AnimeStatus.asMyListItem(): MyListScreenContract.Item {
    return MyListScreenContract.Item(
        this.anime.id,
        this.status.presentIndex,
        this.anime.title,
        this.status.color,
        this.anime.picture?.large,
        this.anime.mediaType,
        this.numWatchedEpisodes,
        this.anime.numEpisodes,
        this.score,
        this.anime.mean,
        this.tags,
        this.comments,
        this.updatedAt,
    )
}
