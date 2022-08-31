package com.kis.youranimelist.ui.mylist

import androidx.compose.ui.graphics.Color
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatusValue

object MyListScreenContract {
    data class ScreenState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val errorText: String? = null,
        val items: List<Item>,
        val tabs: List<String> = AnimeStatusValue.listOfIndices(),
        val currentTab: Int = 0,
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
    )


    interface ScreenEventsListener {
        fun onTabClicked(tab: Int)
        fun onReloadClicked()
        fun onResetStateClicked()
        fun onSwipeRefresh()
    }
}
