package com.kis.youranimelist.domain.user.model

data class UserAnimeStatistic(
    val itemsWatching: Int?,
    val itemsCompleted: Int?,
    val itemsOnHold: Int?,
    val itemsDropped: Int?,
    val itemsPlanToWatch: Int?,
    val items: Int?,
    val daysWatched: Float?,
    val daysWatching: Float?,
    val daysCompleted: Float?,
    val daysOnHold: Float?,
    val daysDropped: Float?,
    val days: Float?,
    val episodes: Int?,
    val meanScore: Float?,
)
