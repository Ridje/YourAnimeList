package com.kis.youranimelist.ui.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.kis.youranimelist.R
import com.kis.youranimelist.ui.Theme

object OnboardingScreenContract {
    data class ScreenState(
        val onboardingCards: List<OnboardingItem> = getOnboardingItems(),
    )

    data class OnboardingItem(
        val backgroundColor: Color,
        @StringRes
        val sign: Int,
        @DrawableRes
        val icon: Int,
    )
}

private fun getOnboardingItems(): List<OnboardingScreenContract.OnboardingItem> {

    val screenZero = OnboardingScreenContract.OnboardingItem(
        backgroundColor = Theme.Colors.background,
        sign = R.string.onboarding_welcome,
        icon = R.drawable.ic_child_solid
    )
    val screenOne = OnboardingScreenContract.OnboardingItem(
        backgroundColor = Theme.Colors.secondary,
        sign = R.string.onboarding_explore,
        icon = R.drawable.ic_explore,
    )
    val screenTwo = OnboardingScreenContract.OnboardingItem(
        backgroundColor = Theme.Colors.background,
        sign = R.string.onboarding_personal_list,
        icon = R.drawable.ic_list_ol
    )
    val screenThree = OnboardingScreenContract.OnboardingItem(
        backgroundColor = Theme.Colors.secondary,
        sign = R.string.onboarding_profile,
        icon = R.drawable.ic_user,
    )
    val screenFour = OnboardingScreenContract.OnboardingItem(
        backgroundColor = Theme.Colors.background,
        sign = R.string.onboarding_settings,
        icon = R.drawable.ic_settings
    )
    return listOf(
        screenZero,
        screenOne,
        screenTwo,
        screenThree,
        screenFour,
    )
}
