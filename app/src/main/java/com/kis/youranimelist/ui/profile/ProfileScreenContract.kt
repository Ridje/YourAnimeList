package com.kis.youranimelist.ui.profile

import androidx.compose.ui.graphics.Color
import com.kis.youranimelist.R
import com.kis.youranimelist.core.ResourceProvider
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatusValue
import com.kis.youranimelist.domain.user.model.User
import com.kis.youranimelist.domain.user.model.UserAnimeStatistic
import me.bytebeats.views.charts.pie.PieChartData
import java.text.DateFormat
import java.util.Date

object ProfileScreenContract {
    data class ScreeState(
        val isLoading: Boolean = true,
        val isError: Boolean = false,
        val user: User? = null,
        val statisticsPieData: PieChartData? = null,
        val legend: List<Pair<String, Color?>>? = null,
        val bottomStatisticsData: BottomStatisticsData? = null,
    )

    data class User(
        val id: Int,
        val name: String,
        val pictureUrl: String?,
        val gender: String?,
        val birthday: String?,
        val location: String?,
        val joinedAt: String?,
        val backgroundUrl: String? = null,
        val pictureAnimeId: Int?,
    )

    data class BottomStatisticsData(
        val days: String?,
        val episodes: String?,
        val meanScore: String?,
    )

    interface ScreenEventsListener {
        fun onReloadClicked()
        fun onResetStateClicked()
    }
}

fun User.asProfileScreenState(
    userMapper: (User) -> ProfileScreenContract.User,
    userStatisticToProfilePieChartMapper: (UserAnimeStatistic?) -> PieChartData,
    userStatisticToProfileLegend: (UserAnimeStatistic?) -> List<Pair<String, Color?>>,
    userStatisticToBottomStatisticData: (UserAnimeStatistic?) -> ProfileScreenContract.BottomStatisticsData,
): ProfileScreenContract.ScreeState {
    return ProfileScreenContract.ScreeState(
        isLoading = false,
        isError = false,
        user = userMapper.invoke(this),
        statisticsPieData = userStatisticToProfilePieChartMapper.invoke(this.userAnimeStatistic),
        legend = userStatisticToProfileLegend.invoke(this.userAnimeStatistic),
        bottomStatisticsData = userStatisticToBottomStatisticData.invoke(this.userAnimeStatistic),
    )
}


fun User.asProfileScreenUser(
    birthDateFormatter: (Date) -> String,
    joinedAtDateFormatter: (Date) -> String,
): ProfileScreenContract.User {
    return ProfileScreenContract.User(
        id = this.id,
        name = this.name,
        pictureUrl = this.picture,
        gender = this.gender,
        birthday = this.birthday?.let {
            birthDateFormatter(it)
        },
        location = this.location,
        joinedAt = this.joinedAt?.let {
            joinedAtDateFormatter(it)
        },
        backgroundUrl = mutableListOf<String?>()
            .plus(this.favouriteAnime?.picture?.large)
            .plus(this.favouriteAnime?.pictures?.map { it.large }
                ?: listOf(
                    null))
            .randomOrNull(),
        pictureAnimeId = this.favouriteAnime?.id,
    )
}

fun Date?.format(formatter: DateFormat): String {
    return this?.let { formatter.format(this) } ?: ""
}

fun UserAnimeStatistic?.asProfilePieChartData(): PieChartData {
    return PieChartData(
        slices = listOf(
            PieChartData.Slice(
                value = this?.itemsCompleted?.toFloat()
                    ?: 0f,
                color = AnimeStatusValue.Completed.color,
            ),
            PieChartData.Slice(
                value = this?.itemsWatching?.toFloat()
                    ?: 0f,
                color = AnimeStatusValue.Watching.color,
            ),
            PieChartData.Slice(
                value = this?.itemsOnHold?.toFloat()
                    ?: 0f,
                color = AnimeStatusValue.OnHold.color,
            ),
            PieChartData.Slice(
                value = this?.itemsDropped?.toFloat()
                    ?: 0f,
                color = AnimeStatusValue.Dropped.color,
            ),
            PieChartData.Slice(
                value = this?.itemsPlanToWatch?.toFloat()
                    ?: 0f,
                color = AnimeStatusValue.PlanToWatch.color,
            )
        )
    )
}

fun UserAnimeStatistic?.asProfileLegend(
    resourceProvider: ResourceProvider,
): List<Pair<String, Color?>> {
    return listOf(
        Pair(resourceProvider.getString(R.string.completed_item,
            this?.itemsCompleted ?: ""),
            AnimeStatusValue.Completed.color),
        Pair(resourceProvider.getString(R.string.watching_items,
            this?.itemsWatching ?: ""),
            AnimeStatusValue.Watching.color),
        Pair(resourceProvider.getString(R.string.on_hold_items,
            this?.itemsOnHold ?: ""),
            AnimeStatusValue.OnHold.color),
        Pair(resourceProvider.getString(R.string.dropped_items,
            this?.itemsDropped ?: ""),
            AnimeStatusValue.Dropped.color),
        Pair(resourceProvider.getString(R.string.plan_to_watch_items,
            this?.itemsPlanToWatch ?: ""),
            AnimeStatusValue.PlanToWatch.color),
        Pair(resourceProvider.getString(R.string.total_items,
            this?.items ?: ""), null),
    )
}

fun UserAnimeStatistic?.asBottomStatisticsData(): ProfileScreenContract.BottomStatisticsData {
    return ProfileScreenContract.BottomStatisticsData(
        days = this?.days.toString(),
        episodes = this?.episodes.toString(),
        meanScore = this?.meanScore.toString(),
    )
}

