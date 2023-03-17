package com.kis.youranimelist.domain.settings

import com.kis.youranimelist.core.utils.AppPreferencesWrapper
import com.kis.youranimelist.core.utils.Setting
import com.kis.youranimelist.core.utils.SortType
import com.kis.youranimelist.domain.cache.ClearCacheUseCase
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    private val appPreferences: AppPreferencesWrapper,
    private val clearCacheUseCase: ClearCacheUseCase,
) {

    fun getAppSettings(): Map<Setting<*>, Boolean> {
        return mapOf(
            Setting.NSFW to appPreferences.readValue(Setting.NSFW),
            Setting.UseAppAuth to appPreferences.readValue(Setting.UseAppAuth)
        )
    }

    fun settingOnboardingShown(): Boolean {
        return appPreferences.readValue(Setting.OnboardingShown)
    }

    fun updateOnboardingShownSetting(value: Boolean) {
        appPreferences.writeValue(Setting.OnboardingShown, value)
    }

    fun settingPersonalListSort(): SortType {
        return appPreferences.readValue(key = Setting.PersonalListSort, defaultValue = SortType.Title)
    }

    fun updatePersonalListSort(value: SortType) {
        appPreferences.writeValue(key = Setting.PersonalListSort, value = value)
    }

    fun updateNSFVSetting(value: Boolean) {
        appPreferences.writeValue(Setting.NSFW, value)
        clearCacheUseCase()
    }
}
