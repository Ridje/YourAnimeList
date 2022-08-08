package com.kis.youranimelist.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.R
import com.kis.youranimelist.core.ResourceProvider
import com.kis.youranimelist.di.Medium
import com.kis.youranimelist.domain.UserUseCase
import com.kis.youranimelist.model.app.AnimeStatusValue
import com.kis.youranimelist.ui.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.bytebeats.views.charts.pie.PieChartData
import java.text.DateFormat
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    @Medium
    private val shortDateFormat: DateFormat,
    private val resourceProvider: ResourceProvider,
) : ViewModel(
) {

    val viewState: MutableStateFlow<ProfileScreenContract.ScreeState> = MutableStateFlow(
        ProfileScreenContract.ScreeState()
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userUseCase.getUserData().collectLatest { resultUser ->
                viewState.value = ProfileScreenContract.ScreeState(
                    isLoading = false,
                    user = User(
                        id = resultUser.id,
                        name = resultUser.name,
                        pictureUrl = resultUser.picture,
                        gender = resultUser.gender,
                        birthday = resultUser.birthday?.let { shortDateFormat.format(resultUser.birthday) },
                        location = resultUser.location,
                        joinedAt = resultUser.joinedAt?.let { shortDateFormat.format(resultUser.joinedAt) },
                        backgroundUrl = mutableListOf<String?>()
                            .plus(resultUser.favouriteAnime?.picture?.large)
                            .plus(resultUser.favouriteAnime?.pictures?.map { it.large } ?: listOf(
                                null))
                            .randomOrNull(),
                    ),
                    statisticsPieData = PieChartData(
                        slices = listOf(
                            PieChartData.Slice(
                                value = resultUser.animeStatistics?.itemsCompleted?.toFloat() ?: 0f,
                                color = AnimeStatusValue.Completed.color,
                            ),
                            PieChartData.Slice(
                                value = resultUser.animeStatistics?.itemsWatching?.toFloat() ?: 0f,
                                color = AnimeStatusValue.Watching.color,
                            ),
                            PieChartData.Slice(
                                value = resultUser.animeStatistics?.itemsOnHold?.toFloat() ?: 0f,
                                color = AnimeStatusValue.OnHold.color,
                            ),
                            PieChartData.Slice(
                                value = resultUser.animeStatistics?.itemsDropped?.toFloat() ?: 0f,
                                color = AnimeStatusValue.Dropped.color,
                            ),
                            PieChartData.Slice(
                                value = resultUser.animeStatistics?.itemsPlanToWatch?.toFloat()
                                    ?: 0f,
                                color = AnimeStatusValue.PlanToWatch.color,
                            )
                        )
                    ),
                    legend = listOf(
                        Pair(resourceProvider.getString(R.string.completed_item,
                            resultUser.animeStatistics?.itemsCompleted ?: ""),
                            AnimeStatusValue.Completed.color),
                        Pair(resourceProvider.getString(R.string.watching_items,
                            resultUser.animeStatistics?.itemsWatching ?: ""),
                            AnimeStatusValue.Watching.color),
                        Pair(resourceProvider.getString(R.string.on_hold_items,
                            resultUser.animeStatistics?.itemsOnHold ?: ""),
                            AnimeStatusValue.OnHold.color),
                        Pair(resourceProvider.getString(R.string.dropped_items,
                            resultUser.animeStatistics?.itemsDropped ?: ""),
                            AnimeStatusValue.Dropped.color),
                        Pair(resourceProvider.getString(R.string.plan_to_watch_items,
                            resultUser.animeStatistics?.itemsPlanToWatch ?: ""),
                            AnimeStatusValue.PlanToWatch.color),
                        Pair(resourceProvider.getString(R.string.total_items,
                            resultUser.animeStatistics?.items ?: ""), null),
                    ),
                    bottomStatisticsData = BottomStatisticsData(
                        days = resultUser.animeStatistics?.days.toString(),
                        episodes = resultUser.animeStatistics?.episodes.toString(),
                        meanScore = resultUser.animeStatistics?.meanScore.toString(),
                    )
                )
            }
        }
    }
}
