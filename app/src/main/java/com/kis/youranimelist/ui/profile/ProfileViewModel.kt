package com.kis.youranimelist.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.R
import com.kis.youranimelist.core.ResourceProvider
import com.kis.youranimelist.di.Medium
import com.kis.youranimelist.domain.Result
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatusValue
import com.kis.youranimelist.domain.user.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
) : ViewModel(), ProfileScreenContract.ScreenEventsListener {

    private val _screenState: MutableStateFlow<ProfileScreenContract.ScreeState> = MutableStateFlow(
        ProfileScreenContract.ScreeState()
    )
    val screenState: StateFlow<ProfileScreenContract.ScreeState>
        get() = _screenState

    init {
        startObserveProfileChanges()
    }

    private fun startObserveProfileChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            userUseCase.getUserData().collectLatest { resultUser ->
                val newValue = when (resultUser) {
                    is Result.Success -> {
                        ProfileScreenContract.ScreeState(
                            isLoading = false,
                            user = User(
                                id = resultUser.data.id,
                                name = resultUser.data.name,
                                pictureUrl = resultUser.data.picture,
                                gender = resultUser.data.gender,
                                birthday = resultUser.data.birthday?.let {
                                    shortDateFormat.format(resultUser.data.birthday)
                                },
                                location = resultUser.data.location,
                                joinedAt = resultUser.data.joinedAt?.let {
                                    shortDateFormat.format(resultUser.data.joinedAt)
                                },
                                backgroundUrl = mutableListOf<String?>()
                                    .plus(resultUser.data.favouriteAnime?.picture?.large)
                                    .plus(resultUser.data.favouriteAnime?.pictures?.map { it.large }
                                        ?: listOf(
                                            null))
                                    .randomOrNull(),
                                pictureAnimeId = resultUser.data.favouriteAnime?.id,
                            ),
                            statisticsPieData = PieChartData(
                                slices = listOf(
                                    PieChartData.Slice(
                                        value = resultUser.data.userAnimeStatistic?.itemsCompleted?.toFloat()
                                            ?: 0f,
                                        color = AnimeStatusValue.Completed.color,
                                    ),
                                    PieChartData.Slice(
                                        value = resultUser.data.userAnimeStatistic?.itemsWatching?.toFloat()
                                            ?: 0f,
                                        color = AnimeStatusValue.Watching.color,
                                    ),
                                    PieChartData.Slice(
                                        value = resultUser.data.userAnimeStatistic?.itemsOnHold?.toFloat()
                                            ?: 0f,
                                        color = AnimeStatusValue.OnHold.color,
                                    ),
                                    PieChartData.Slice(
                                        value = resultUser.data.userAnimeStatistic?.itemsDropped?.toFloat()
                                            ?: 0f,
                                        color = AnimeStatusValue.Dropped.color,
                                    ),
                                    PieChartData.Slice(
                                        value = resultUser.data.userAnimeStatistic?.itemsPlanToWatch?.toFloat()
                                            ?: 0f,
                                        color = AnimeStatusValue.PlanToWatch.color,
                                    )
                                )
                            ),
                            legend = listOf(
                                Pair(resourceProvider.getString(R.string.completed_item,
                                    resultUser.data.userAnimeStatistic?.itemsCompleted ?: ""),
                                    AnimeStatusValue.Completed.color),
                                Pair(resourceProvider.getString(R.string.watching_items,
                                    resultUser.data.userAnimeStatistic?.itemsWatching ?: ""),
                                    AnimeStatusValue.Watching.color),
                                Pair(resourceProvider.getString(R.string.on_hold_items,
                                    resultUser.data.userAnimeStatistic?.itemsOnHold ?: ""),
                                    AnimeStatusValue.OnHold.color),
                                Pair(resourceProvider.getString(R.string.dropped_items,
                                    resultUser.data.userAnimeStatistic?.itemsDropped ?: ""),
                                    AnimeStatusValue.Dropped.color),
                                Pair(resourceProvider.getString(R.string.plan_to_watch_items,
                                    resultUser.data.userAnimeStatistic?.itemsPlanToWatch ?: ""),
                                    AnimeStatusValue.PlanToWatch.color),
                                Pair(resourceProvider.getString(R.string.total_items,
                                    resultUser.data.userAnimeStatistic?.items ?: ""), null),
                            ),
                            bottomStatisticsData = BottomStatisticsData(
                                days = resultUser.data.userAnimeStatistic?.days.toString(),
                                episodes = resultUser.data.userAnimeStatistic?.episodes.toString(),
                                meanScore = resultUser.data.userAnimeStatistic?.meanScore.toString(),
                            )
                        )
                    }
                    is Result.Loading -> {
                        ProfileScreenContract.ScreeState(isLoading = true)
                    }
                    is Result.Error -> {
                        _screenState.value.copy(
                            isLoading = false,
                            isError = true,
                        )
                    }
                }
                _screenState.value = newValue
            }
        }
    }

    override fun onReloadClicked() {
        startObserveProfileChanges()
    }

    override fun onResetStateClicked() {
        _screenState.value = _screenState.value.copy(isLoading = false, isError = false)
    }
}
