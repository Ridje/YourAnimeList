package com.kis.youranimelist.ui.mylist

import androidx.compose.ui.graphics.Color

object MyListScreenContract {
    data class ScreenState(
        val isLoading: Boolean = false,
        val items: List<Item>,
        val tabs: List<String> = listOf(
            "all",
            "watching",
            "completed",
            "on_hold",
            "dropped",
            "plan_to_watch",
        ),
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
}
