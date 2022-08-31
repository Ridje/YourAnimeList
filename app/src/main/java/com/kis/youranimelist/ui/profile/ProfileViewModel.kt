package com.kis.youranimelist.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.core.ResourceProvider
import com.kis.youranimelist.di.Medium
import com.kis.youranimelist.domain.model.ResultWrapper
import com.kis.youranimelist.domain.user.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
            userUseCase.getUserData().collect { resultUser ->
                val newValue = when (resultUser) {
                    is ResultWrapper.Success -> {
                        resultUser.data.asProfileScreenState(
                            userMapper = {
                                resultUser.data.asProfileScreenUser(
                                    birthDateFormatter = {
                                        resultUser.data.birthday.format(shortDateFormat)
                                    },
                                    joinedAtDateFormatter = {
                                        resultUser.data.joinedAt.format(shortDateFormat)
                                    },
                                )
                            },
                            userStatisticToProfilePieChartMapper = { resultUser.data.userAnimeStatistic.asProfilePieChartData() },
                            userStatisticToProfileLegend = { resultUser.data.userAnimeStatistic.asProfileLegend(resourceProvider) },
                            userStatisticToBottomStatisticData = { resultUser.data.userAnimeStatistic.asBottomStatisticsData() }
                        )

                    }
                    is ResultWrapper.Loading -> {
                        ProfileScreenContract.ScreeState(isLoading = true)
                    }
                    is ResultWrapper.Error -> {
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
