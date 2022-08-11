package com.kis.youranimelist.domain.user.mapper

import com.kis.youranimelist.data.cache.model.UserPersistence
import com.kis.youranimelist.data.network.model.UserResponse
import com.kis.youranimelist.domain.user.model.User
import java.text.SimpleDateFormat
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
