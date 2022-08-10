package com.kis.youranimelist.domain.personalanimelist.model

import androidx.compose.ui.graphics.Color
import com.kis.youranimelist.ui.Theme

sealed class AnimeStatusValue(val presentIndex: String, val color: Color) {
    object Watching : AnimeStatusValue("watching", Theme.Colors.watchingItemColor)
    object Completed : AnimeStatusValue("completed", Theme.Colors.completedItemColor)
    object OnHold : AnimeStatusValue("on_hold", Theme.Colors.onHoldItemColor)
    object Dropped : AnimeStatusValue("dropped", Theme.Colors.droppedItemColor)
    object PlanToWatch : AnimeStatusValue("plan_to_watch", Theme.Colors.planToWatchItemColor)
    object Unknown : AnimeStatusValue("unknown", Color.Gray)

    object Fabric {
        fun getAnimeStatusByValue(value: String): AnimeStatusValue {
            return when (value) {
                "watching" -> Watching
                "completed" -> Completed
                "on_hold" -> OnHold
                "dropped" -> Dropped
                "plan_to_watch" -> PlanToWatch
                else -> Unknown
            }
        }
    }
}

