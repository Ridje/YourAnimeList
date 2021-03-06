package com.kis.youranimelist.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.kis.youranimelist.BuildConfig
import com.kis.youranimelist.NavigationKeys
import com.kis.youranimelist.R
import com.kis.youranimelist.utils.Pkce
import com.kis.youranimelist.utils.Urls
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach

@Composable
fun LoginScreenRoute(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val loginState by viewModel.screenState.collectAsState()
    val effectFlow = viewModel.effectStream
    val eventsConsumer: LoginScreenContract.LoginScreenEventsConsumer = viewModel
    LoginScreen(
        loginState.webViewVisible,
        loginState.isLoading,
        effectFlow,
        if (loginState.isLoading) {
            {}
        } else {
            eventsConsumer::onLoginClick
        },
        eventsConsumer::onLoginSucceed,
        {
            navController.popBackStack()
            navController.navigate(NavigationKeys.Route.EXPLORE)
        },
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginScreen(
    isWebViewVisible: Boolean,
    isLoading: Boolean,
    effectFlow: Flow<LoginScreenContract.Effect>,
    onLoginClick: () -> Unit,
    onLoginSucceed: (String, String) -> Unit,
    onAuthDataSaved: () -> Unit,
) {

    LaunchedEffect(effectFlow) {
        effectFlow.onEach { effect ->
            when (effect) {
                is LoginScreenContract.Effect.AuthDataSaved -> onAuthDataSaved.invoke()
            }
        }.collect {}
    }
    Scaffold { paddingValues ->
        if (isWebViewVisible) {
            val codeVerifier = Pkce.generateCodeVerifier()
            val webViewState =
                rememberWebViewState(url = "${Urls.oauthBaseUrl}authorize?response_type=code&client_id=${BuildConfig.CLIENT_ID}&code_challenge=${codeVerifier}")
            WebView(
                state = webViewState,
                client = LoginWebViewClient(
                    onLoginSucceed,
                    codeVerifier,
                ),
                onCreated = { it.settings.javaScriptEnabled = true }
            )
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(paddingValues),
            ) {
                Text(
                    stringResource(R.string.app_name),
                )
                TextButton(onClick = {
                    onLoginClick()
                }) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(text = stringResource(id = R.string.login))
                    }
                }
            }
        }
    }
}

@Preview(
    showSystemUi = true
)
@Composable
fun LoginScreenPreview() {
    LoginScreen(false, false, MutableSharedFlow(), {}, { _, _ -> }, {})
}

