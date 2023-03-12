package com.kis.youranimelist.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.core.utils.Setting
import com.kis.youranimelist.domain.cache.RefreshCacheUseCase
import com.kis.youranimelist.domain.settings.SettingsUseCase
import com.kis.youranimelist.domain.user.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val userUseCase: UserUseCase,
    private val refreshCacheUseCase: RefreshCacheUseCase,
) : ViewModel(), SettingsScreenContract.ScreenEventsListener {

    private val _screenState = MutableStateFlow(
        SettingsScreenContract.ScreenState(
            nsfw = false,
            useAppAuth = false
        )
    )

    val screenState = _screenState as StateFlow<SettingsScreenContract.ScreenState>

    init {
        viewModelScope.launch {
            val appSettings = settingsUseCase.getAppSettings()
            _screenState.value = SettingsScreenContract.ScreenState(
                nsfw = appSettings[Setting.NSFW] ?: false,
                useAppAuth = appSettings[Setting.UseAppAuth] ?: false,
            )
        }
    }

    override fun onNsfwChanged(newValue: Boolean) {
        viewModelScope.launch {
            settingsUseCase.updateNSFVSetting(newValue)
            refreshCacheUseCase.onEttiSettingSwitched()
            _screenState.value = _screenState.value.copy(
                nsfw = newValue,
            )
        }
    }

    override fun onLogoutClicked() {
        viewModelScope.launch {
            userUseCase.logOut()
        }
    }
}
