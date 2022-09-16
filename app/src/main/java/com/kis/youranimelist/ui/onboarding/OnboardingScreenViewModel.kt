package com.kis.youranimelist.ui.onboarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingScreenViewModel @Inject constructor(

) : ViewModel() {

    private val _screenState: MutableStateFlow<OnboardingScreenContract.ScreenState> =
        MutableStateFlow(
            OnboardingScreenContract.ScreenState())
    val screenState: StateFlow<OnboardingScreenContract.ScreenState>
        get() = _screenState

}
