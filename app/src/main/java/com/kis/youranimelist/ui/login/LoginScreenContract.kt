package com.kis.youranimelist.ui.login

object LoginScreenContract {
    data class ScreenState(
        val webViewVisible: Boolean = false,
        val isLoading: Boolean = false,
        val isLoadingUserDatabase: Boolean = false,
    )

    sealed class Effect {
        object AuthDataSaved: Effect()
        object NetworkError: Effect()
        object AuthDataSavedShowOnboarding: Effect()
    }

    interface LoginScreenEventsConsumer {
        fun onLoginClick()
        fun onLoginSucceed(token: String, codeVerifier: String)
        fun onBackOnWebView()
        fun onAuthorizationSkipped()
    }
}
