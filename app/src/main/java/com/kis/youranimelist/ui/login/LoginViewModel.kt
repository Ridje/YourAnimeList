package com.kis.youranimelist.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.BuildConfig
import com.kis.youranimelist.network.AuthInterceptor
import com.kis.youranimelist.repository.RemoteDataSource
import com.kis.youranimelist.utils.AppPreferences
import com.kis.youranimelist.utils.AppPreferences.Companion.ACCESS_TOKEN_SETTING_KEY
import com.kis.youranimelist.utils.AppPreferences.Companion.EXPIRES_IN_TOKEN_SETTING_KEY
import com.kis.youranimelist.utils.AppPreferences.Companion.REFRESH_TOKEN_SETTING_KEY
import com.kis.youranimelist.utils.AppPreferences.Companion.TYPE_TOKEN_SETTING_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val appPreferences: AppPreferences,
    private val authInterceptor: AuthInterceptor,
) : ViewModel(),
    LoginScreenContract.LoginScreenEventsConsumer {

    val screenState: MutableStateFlow<LoginScreenContract.ScreenState> = MutableStateFlow(
        LoginScreenContract.ScreenState(webViewVisible = false, isLoading = true)
    )

    val effectStream: MutableSharedFlow<LoginScreenContract.Effect> = MutableSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            delay(500L)
            if (authInterceptor.authorizationValid()) {
                effectStream.emit(LoginScreenContract.Effect.AuthDataSaved)
            } else {
                screenState.value = screenState.value.copy(isLoading = false)
            }
        }
    }

    override fun onLoginClick() {
        screenState.value = LoginScreenContract.ScreenState(webViewVisible = true, isLoading = true)
    }

    override fun onLoginSucceed(
        token: String,
        codeVerifier: String,
    ) {
        screenState.value = LoginScreenContract.ScreenState(webViewVisible = false, isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val postResult = remoteDataSource.getAccessToken(BuildConfig.CLIENT_ID,
                token,
                codeVerifier,
                "authorization_code")
            appPreferences.writeString(ACCESS_TOKEN_SETTING_KEY,
                postResult.accessToken)
            appPreferences.writeString(REFRESH_TOKEN_SETTING_KEY,
                postResult.refreshToken)
            appPreferences.writeInt(EXPIRES_IN_TOKEN_SETTING_KEY,
                postResult.expiresIn)
            appPreferences.writeString(TYPE_TOKEN_SETTING_KEY,
                postResult.tokenType)
            authInterceptor.setAuthorization(
                postResult.tokenType,
                postResult.accessToken,
            )
            effectStream.emit(LoginScreenContract.Effect.AuthDataSaved)
        }
    }
}
