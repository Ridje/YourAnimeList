package com.kis.youranimelist.ui.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.kis.youranimelist.BuildConfig
import com.kis.youranimelist.data.SyncWorker
import com.kis.youranimelist.domain.auth.AuthUseCase
import com.kis.youranimelist.domain.model.ResultWrapper
import com.kis.youranimelist.domain.personalanimelist.PersonalAnimeListUseCase
import com.kis.youranimelist.domain.settings.SettingsUseCase
import com.kis.youranimelist.ui.navigation.NavigationKeys
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOADING_START_DELAY = 500L

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUsecase: AuthUseCase,
    private val settingsUseCase: Lazy<SettingsUseCase>,
    private val personalAnimeList: PersonalAnimeListUseCase,
    private val workManager: WorkManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel(),
    LoginScreenContract.LoginScreenEventsConsumer {

    private val _screenState: MutableStateFlow<LoginScreenContract.ScreenState> = MutableStateFlow(
        LoginScreenContract.ScreenState(webViewVisible = false, isLoading = true)
    )
    val screenState = _screenState as StateFlow<LoginScreenContract.ScreenState>

    private val _effectStream: MutableSharedFlow<LoginScreenContract.Effect> = MutableSharedFlow()
    val effectStream = _effectStream as SharedFlow<LoginScreenContract.Effect>

    private val forceAuth = savedStateHandle.get<Boolean>(NavigationKeys.Argument.FORCE_AUTH) ?: false
    init {
        viewModelScope.launch(Dispatchers.IO) {
            delay(LOADING_START_DELAY)
            if (!forceAuth && authUsecase.isAuthDataValid()) {
                if (authUsecase.isClientAuth()) {
                    workManager.enqueueUniqueWork(
                        SyncWorker.SyncWorkName,
                        ExistingWorkPolicy.REPLACE,
                        SyncWorker.startSyncJob()
                    )
                }
                proceedToNextScreen()
            } else {
                _screenState.value = screenState.value.copy(isLoading = false)
            }
        }
    }

    override fun onLoginClick() {
        _screenState.value =
            LoginScreenContract.ScreenState(webViewVisible = true, isLoading = true)
    }

    override fun onLoginSucceed(
        token: String,
        codeVerifier: String,
    ) {
        _screenState.value =
            LoginScreenContract.ScreenState(webViewVisible = false, isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = authUsecase.getAccessToken(
                BuildConfig.CLIENT_ID,
                token,
                codeVerifier)
            if (result is ResultWrapper.Success) {
                val (accessToken, refreshToken, expiresIn, tokenType) = result.data

                authUsecase.setAuthData(accessToken, refreshToken, expiresIn, tokenType)
                _screenState.value = screenState.value.copy(isLoadingUserDatabase = true)
                personalAnimeList.refreshPersonalAnimeStatuses()
                _screenState.value = screenState.value.copy(isLoadingUserDatabase = false)

                proceedToNextScreen()
            } else {
                _effectStream.emit(LoginScreenContract.Effect.NetworkError)
            }
        }
    }

    private suspend fun proceedToNextScreen() {
        if (settingsUseCase.get().settingOnboardingShown()) {
            _effectStream.emit(LoginScreenContract.Effect.AuthDataSaved)
        } else {
            settingsUseCase.get().updateOnboardingShownSetting(true)
            _effectStream.emit(LoginScreenContract.Effect.AuthDataSavedShowOnboarding)
        }
    }

    override fun onBackOnWebView() {
        _screenState.value =
            LoginScreenContract.ScreenState(webViewVisible = false, isLoading = false)
    }

    override fun onAuthorizationSkipped() {
        _screenState.value =
            LoginScreenContract.ScreenState(webViewVisible = false, isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            authUsecase.setAuthData(BuildConfig.CLIENT_ID)
            proceedToNextScreen()
        }
    }
}
