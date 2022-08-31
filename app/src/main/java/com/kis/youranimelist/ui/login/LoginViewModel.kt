package com.kis.youranimelist.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.kis.youranimelist.BuildConfig
import com.kis.youranimelist.data.SyncWorker
import com.kis.youranimelist.domain.auth.AuthUseCase
import com.kis.youranimelist.data.repository.RemoteDataSource
import com.kis.youranimelist.domain.personalanimelist.PersonalAnimeListUseCase
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
    private val authUsecase: AuthUseCase,
    private val useCase: PersonalAnimeListUseCase,
    private val workManager: WorkManager,
) : ViewModel(),
    LoginScreenContract.LoginScreenEventsConsumer {

    val screenState: MutableStateFlow<LoginScreenContract.ScreenState> = MutableStateFlow(
        LoginScreenContract.ScreenState(webViewVisible = false, isLoading = true)
    )

    val effectStream: MutableSharedFlow<LoginScreenContract.Effect> = MutableSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            delay(500L)
            if (authUsecase.isAuthDataValid()) {
                workManager.enqueueUniqueWork(
                    SyncWorker.SyncWorkName,
                    ExistingWorkPolicy.REPLACE,
                    SyncWorker.startSyncJob()
                )
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
        screenState.value =
            LoginScreenContract.ScreenState(webViewVisible = false, isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val postResult = remoteDataSource.getAccessToken(BuildConfig.CLIENT_ID,
                token,
                codeVerifier,
                "authorization_code")
            authUsecase.setAuthData(postResult.accessToken,
                postResult.refreshToken,
                postResult.expiresIn,
                postResult.tokenType)
            if (authUsecase.isAuthDataValid()) {
                screenState.value = screenState.value.copy(
                    isLoadingUserDatabase = true
                )
                useCase.refreshPersonalAnimeStatuses()
                screenState.value = screenState.value.copy(
                    isLoadingUserDatabase = false
                )
            }
            effectStream.emit(LoginScreenContract.Effect.AuthDataSaved)
        }
    }
}
