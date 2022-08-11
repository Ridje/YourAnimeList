package com.kis.youranimelist.ui.profile

import androidx.compose.ui.graphics.Color
import me.bytebeats.views.charts.pie.PieChartData

object ProfileScreenContract {
    data class ScreeState(
        val isLoading: Boolean = true,
        val isError: Boolean = false,
        val user: User? = null,
        val statisticsPieData: PieChartData? = null,
        val legend: List<Pair<String, Color?>>? = null,
        val bottomStatisticsData: BottomStatisticsData? = null,
    )

    interface ScreenEventsListener {
        fun onReloadClicked()
        fun onResetStateClicked()
    }
}

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
