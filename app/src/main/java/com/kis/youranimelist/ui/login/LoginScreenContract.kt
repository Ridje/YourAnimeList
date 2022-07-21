package com.kis.youranimelist.ui.login

object LoginScreenContract {
    data class ScreenState(
        val webViewVisible: Boolean = false,
    )

    sealed class Effect {
        object AuthDataSaved: Effect()
    }

    interface LoginScreenEventsConsumer {
        fun onLoginClick()
        fun onLoginSucceed(token: String, codeVerifier: String)
    }
}
