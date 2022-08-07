package com.kis.youranimelist.model.app

sealed class AnimeStatusValue {
    object Watching : AnimeStatusValue()
    object Completed : AnimeStatusValue()
    object OnHold : AnimeStatusValue()
    object Dropped : AnimeStatusValue()
    object PlanToWatch : AnimeStatusValue()
    object Unknown : AnimeStatusValue()

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

