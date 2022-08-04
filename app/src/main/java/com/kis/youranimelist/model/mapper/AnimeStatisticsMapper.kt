package com.kis.youranimelist.model.mapper

import com.kis.youranimelist.model.api.AnimeStatisticsResponse
import com.kis.youranimelist.model.app.AnimeStatistics
import javax.inject.Inject


class AnimeStatisticsMapper @Inject constructor() {
    fun map(from: AnimeStatisticsResponse?): AnimeStatistics? {
        if (from == null) {
            return null
        }

        return AnimeStatistics(
            itemsWatching = from.itemsWatching,
            itemsCompleted = from.itemsCompleted,
            itemsOnHold = from.itemsOnHold,
            itemsDropped = from.itemsDropped,
            itemsPlanToWatch = from.itemsPlanToWatch,
            items = from.items,
            daysWatched = from.daysWatched,
            daysWatching = from.daysWatching,
            daysCompleted = from.daysCompleted,
            daysOnHold = from.daysOnHold,
            daysDropped = from.daysDropped,
            days = from.days,
            episodes = from.episodes,
            meanScore = from.meanScore,
        )
    }
}
