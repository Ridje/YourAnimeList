package com.kis.youranimelist.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeStatisticsResponse(
    @SerialName("num_items_watching") val itemsWatching: Int?,
    @SerialName("num_items_completed") val itemsCompleted: Int?,
    @SerialName("num_items_on_hold") val itemsOnHold: Int?,
    @SerialName("num_items_dropped") val itemsDropped: Int?,
    @SerialName("num_items_plan_to_watch") val itemsPlanToWatch: Int?,
    @SerialName("num_items") val items: Int?,
    @SerialName("num_days_watched") val daysWatched: Float?,
    @SerialName("num_days_watching") val daysWatching: Float?,
    @SerialName("num_days_completed") val daysCompleted: Float?,
    @SerialName("num_days_on_hold") val daysOnHold: Float?,
    @SerialName("num_days_dropped") val daysDropped: Float?,
    @SerialName("num_days") val days: Float?,
    @SerialName("num_episodes") val episodes: Int?,
    @SerialName("mean_score") val meanScore: Float?,
)
