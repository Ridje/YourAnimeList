package com.kis.youranimelist.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
        effectFlow,
        eventsConsumer::onLoginClick,
        eventsConsumer::onLoginSucceed,
        { navController.navigate(NavigationKeys.Route.EXPLORE) }
    )
}

@Composable
fun LoginScreen(
    isWebViewVisible: Boolean,
    effectFlow: Flow<LoginScreenContract.Effect>,
    onLoginClick: () -> Unit,
    onLoginSucceed: (String, String) -> Unit,
    onAuthDataSaved: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(effectFlow) {
        effectFlow.onEach { effect ->
            when (effect) {
                is LoginScreenContract.Effect.AuthDataSaved -> onAuthDataSaved.invoke()
            }
        }.collect {
            snackbarHostState.showSnackbar(
                "Data saved!!!",
                "GO FURTHER",
                SnackbarDuration.Indefinite,
            )
        }
    }
    Scaffold(scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (isWebViewVisible) {
                val codeVerifier = Pkce.generateCodeVerifier()
                val webViewState =
                    rememberWebViewState(url = "${Urls.oauthBaseUrl}authorize?response_type=code&client_id=${BuildConfig.CLIENT_ID}&code_challenge=${codeVerifier}")
                WebView(
                    state = webViewState,
                    client = LoginWebViewComponent(
                        onLoginSucceed,
                        codeVerifier,
                    ),
                    onCreated = { it.settings.javaScriptEnabled = true }
                )
            } else {
                Text(
                    stringResource(R.string.app_name),
                )
                TextButton(onClick = { onLoginClick() }) {
                    Text(text = stringResource(id = R.string.login))
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
    LoginScreen(false, MutableSharedFlow(), {}, { _, _ -> }, {})
}

