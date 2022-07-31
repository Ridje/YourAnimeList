package com.kis.youranimelist.ui

import androidx.lifecycle.ViewModel
import com.kis.youranimelist.domain.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    authUsecase: AuthUseCase,
) : ViewModel() {
    val navigateEffects = authUsecase.observerAuthErrors()
}
