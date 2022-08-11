package com.kis.youranimelist.domain.user.mapper

import com.kis.youranimelist.data.cache.model.UserPersistence
import com.kis.youranimelist.data.network.model.AnimeStatisticsResponse
import com.kis.youranimelist.domain.user.model.UserAnimeStatistic
import javax.inject.Inject


class UserAnimeStatisticMapper @Inject constructor() {
    fun map(from: AnimeStatisticsResponse?): UserAnimeStatistic? {
        if (from == null) {
            return null
        }

        return UserAnimeStatistic(
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

    fun map(from: UserPersistence): UserAnimeStatistic {
        return UserAnimeStatistic(
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
