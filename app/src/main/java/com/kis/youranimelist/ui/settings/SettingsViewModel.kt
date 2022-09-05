package com.kis.youranimelist.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.core.utils.Setting
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
) : ViewModel(), SettingsScreenContract.ScreenEventsListener {

    private val _screenState = MutableStateFlow(
        SettingsScreenContract.ScreenState(
            nsfw = false,
            true
        )
    )

    val screenState = _screenState as StateFlow<SettingsScreenContract.ScreenState>

    init {
        viewModelScope.launch {
            _screenState.value = SettingsScreenContract.ScreenState(
                nsfw = settingsUseCase.getAppSettings()[Setting.NSFW] ?: false,
                true,
            )
        }
    }

    override fun onNsfwChanged(newValue: Boolean) {
        viewModelScope.launch {
            settingsUseCase.updateNSFVSetting(newValue)
            _screenState.value = _screenState.value.copy(
                nsfw = newValue
            )
        }
    }

    override fun onLogoutClicked() {
        viewModelScope.launch {
            userUseCase.logOut()
        }
    }
}
