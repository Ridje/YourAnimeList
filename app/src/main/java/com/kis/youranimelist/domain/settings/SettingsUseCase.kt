package com.kis.youranimelist.domain.settings

import com.kis.youranimelist.core.utils.AppPreferencesWrapper
import com.kis.youranimelist.core.utils.Setting
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    private val appPreferences: AppPreferencesWrapper,
) {

    fun getAppSettings(): Map<Setting<*>, Boolean> {
        return mapOf(
            Setting.NSFW to appPreferences.readValue(Setting.NSFW)
        )
    }

    fun settingOnboardingShown(): Boolean {
        return appPreferences.readValue(Setting.OnboardingShown)
    }

    fun updateOnboardingShownSetting(value: Boolean) {
        appPreferences.writeValue(Setting.OnboardingShown, value)
    }

    fun updateNSFVSetting(value: Boolean) {
        appPreferences.writeValue(Setting.NSFW, value)
    }
}
