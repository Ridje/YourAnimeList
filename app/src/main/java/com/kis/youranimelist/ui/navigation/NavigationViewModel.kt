package com.kis.youranimelist.ui.navigation

import androidx.lifecycle.ViewModel
import com.kis.youranimelist.domain.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    authUseCase: AuthUseCase,
) : ViewModel() {
    val navigateEffects = authUseCase.observerAuthErrors()
}
