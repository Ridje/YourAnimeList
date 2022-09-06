package com.kis.youranimelist.domain.user.mapper

import com.kis.youranimelist.data.cache.model.UserPersistence
import com.kis.youranimelist.data.network.model.UserResponse
import com.kis.youranimelist.domain.user.model.User
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class UserMapper @Inject constructor(
    private val userAnimeStatisticMapper: UserAnimeStatisticMapper,
) {
    companion object {
        val format by lazy { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'") }
        val bdayFormat by lazy { SimpleDateFormat("yyyy-MM-dd") }
    }

    fun map(from: UserResponse): User {
        return User(
            id = from.id,
            name = from.name,
            picture = from.picture,
            gender = from.gender,
            birthday = from.birthday?.let { birthday -> bdayFormat.parse(birthday) },
            location = from.location,
            joinedAt = from.joinedAt?.let { joinedAt -> format.parse(joinedAt) },
            userAnimeStatistic = userAnimeStatisticMapper.map(from.animeStatistics)
        )
    }

    fun map(from: UserPersistence): User {
        return User(
            id = from.id,
            name = from.name,
            gender = from.gender,
            birthday = from.birthday?.let { birthday -> bdayFormat.parse(birthday) },
            location = from.location,
            joinedAt = from.joinedAt?.let { joinedAt -> format.parse(joinedAt) },
            picture = from.picture,
            userAnimeStatistic = userAnimeStatisticMapper.map(from),
        )
    }
}

fun User.asUserPersistence(
    bdayFormatter: (Date?) -> String? = { date ->
        date?.let {
            UserMapper.bdayFormat.format(date)
        } ?: ""
    },
    shortFormatter: (Date?) -> String? = { date ->
        date?.let {
            UserMapper.format.format(date)
        } ?: ""
    },
): UserPersistence {
    return UserPersistence(
        id = this.id,
        name = this.name,
        gender = this.gender,
        birthday = bdayFormatter.invoke(this.birthday),
        location = this.location,
        joinedAt = shortFormatter.invoke(this.joinedAt),
        itemsWatching = this.userAnimeStatistic?.itemsWatching,
        itemsCompleted = this.userAnimeStatistic?.itemsCompleted,
        itemsOnHold = this.userAnimeStatistic?.itemsOnHold,
        itemsDropped = this.userAnimeStatistic?.itemsDropped,
        itemsPlanToWatch = this.userAnimeStatistic?.itemsPlanToWatch,
        items = this.userAnimeStatistic?.items,
        daysWatched = this.userAnimeStatistic?.daysWatched,
        daysWatching = this.userAnimeStatistic?.daysWatching,
        daysCompleted = this.userAnimeStatistic?.daysCompleted,
        daysOnHold = this.userAnimeStatistic?.daysOnHold,
        daysDropped = this.userAnimeStatistic?.daysDropped,
        days = this.userAnimeStatistic?.days,
        episodes = this.userAnimeStatistic?.episodes,
        meanScore = this.userAnimeStatistic?.meanScore,
        picture = this.picture,
    )
}
