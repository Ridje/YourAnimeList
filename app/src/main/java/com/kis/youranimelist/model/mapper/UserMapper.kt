package com.kis.youranimelist.model.mapper

import com.kis.youranimelist.model.api.UserResponse
import com.kis.youranimelist.model.app.AnimeStatistics
import com.kis.youranimelist.model.app.User
import java.text.SimpleDateFormat
import javax.inject.Inject


class UserMapper @Inject constructor(
    private val animeStatisticsMapper: AnimeStatisticsMapper,
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
            animeStatistics = animeStatisticsMapper.map(from.animeStatistics)
        )
    }
}
