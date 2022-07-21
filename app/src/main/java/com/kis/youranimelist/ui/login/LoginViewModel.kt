package com.kis.youranimelist.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kis.youranimelist.BuildConfig
import com.kis.youranimelist.network.AuthInterceptor
import com.kis.youranimelist.repository.RepositoryNetwork
import com.kis.youranimelist.utils.AppPreferences
import com.kis.youranimelist.utils.AppPreferences.Companion.ACCESS_TOKEN_SETTING_KEY
import com.kis.youranimelist.utils.AppPreferences.Companion.EXPIRES_IN_TOKEN_SETTING_KEY
import com.kis.youranimelist.utils.AppPreferences.Companion.REFRESH_TOKEN_SETTING_KEY
import com.kis.youranimelist.utils.AppPreferences.Companion.TYPE_TOKEN_SETTING_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repositoryNetwork: RepositoryNetwork,
    private val appPreferences: AppPreferences,
    private val authInterceptor: AuthInterceptor,
) : ViewModel(),
    LoginScreenContract.LoginScreenEventsConsumer {

    val screenState: MutableStateFlow<LoginScreenContract.ScreenState> = MutableStateFlow(
        LoginScreenContract.ScreenState(false)
    )

    val effectStream: MutableSharedFlow<LoginScreenContract.Effect> = MutableSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (authInterceptor.authorizationValid()) {
                effectStream.emit(LoginScreenContract.Effect.AuthDataSaved)
            }
        }
    }

    override fun onLoginClick() {
        screenState.value = LoginScreenContract.ScreenState(true);
    }

    override fun onLoginSucceed(
        token: String,
        codeVerifier: String,
    ) {
        screenState.value = LoginScreenContract.ScreenState(false)
        viewModelScope.launch(Dispatchers.IO) {
            val postResult = repositoryNetwork.getAccessToken(BuildConfig.CLIENT_ID,
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
