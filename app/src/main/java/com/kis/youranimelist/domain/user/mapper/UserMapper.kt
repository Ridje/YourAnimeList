package com.kis.youranimelist.domain.user.mapper

import com.kis.youranimelist.data.network.model.UserResponse
import com.kis.youranimelist.domain.user.model.User
import java.text.SimpleDateFormat
import javax.inject.Inject


class UserMapper @Inject constructor(
    private val userAnimeStatisticMapper: UserAnimeStatisticMapper,
) {
    fun map(from: UserResponse): User {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'")
        val bdayFormat = SimpleDateFormat("yyyy-MM-dd")
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
}
