package com.kis.youranimelist.ui.login

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.kis.youranimelist.BuildConfig
import com.kis.youranimelist.R
import com.kis.youranimelist.core.utils.Pkce
import com.kis.youranimelist.core.utils.Urls
import com.kis.youranimelist.ui.navigation.NavigationKeys
import com.kis.youranimelist.ui.widget.TextProgressIndicator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreenRoute(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val loginState by viewModel.screenState.collectAsState()
    val effectFlow = viewModel.effectStream
    val eventsConsumer: LoginScreenContract.LoginScreenEventsConsumer = viewModel
    LoginScreen(
        isWebViewVisible = loginState.webViewVisible,
        isLoading = loginState.isLoading,
        isLoadingUserDatabase = loginState.isLoadingUserDatabase,
        effectFlow = effectFlow,
        onLoginClick = if (loginState.isLoading) {
            {}
        } else {
            eventsConsumer::onLoginClick
        },
        onLoginSucceed = eventsConsumer::onLoginSucceed,
        onAuthDataSaved = {
            navController.navigate(NavigationKeys.Route.EXPLORE) {
                popUpTo(0)
            }
        },
        onAuthorizationSkipped = eventsConsumer::onAuthorizationSkipped,
        onAuthDataSavedBoardingNowShownYet = {
            navController.navigate(NavigationKeys.Route.EXPLORE) {
                popUpTo(0)
            }
            navController.navigate(NavigationKeys.Route.ONBOARDING)
        },
        onBackOnWebView = eventsConsumer::onBackOnWebView,
    )
}
@Composable
fun LoginScreen(
    isWebViewVisible: Boolean,
    isLoading: Boolean,
    isLoadingUserDatabase: Boolean,
    effectFlow: Flow<LoginScreenContract.Effect>,
    onLoginClick: () -> Unit = {},
    onLoginSucceed: (String, String) -> Unit = { _, _ -> },
    onAuthDataSaved: () -> Unit = {},
    onAuthorizationSkipped: () -> Unit = {},
    onAuthDataSavedBoardingNowShownYet: () -> Unit = {},
    onBackOnWebView: () -> Unit = {},
) {
    if (isWebViewVisible) {
        BackHandler(onBack = onBackOnWebView)
    }
    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is LoginScreenContract.Effect.AuthDataSaved -> onAuthDataSaved.invoke()
                LoginScreenContract.Effect.NetworkError -> TODO()
                LoginScreenContract.Effect.AuthDataSavedShowOnboarding -> onAuthDataSavedBoardingNowShownYet.invoke()
            }
        }
    }

    if (isWebViewVisible) {
        LoginWebView(onLoginSucceed = onLoginSucceed)
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.h4
                )
                TextButton(onClick = {
                    onLoginClick()
                }) {
                    if (isLoading) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            if (isLoadingUserDatabase) {
                                TextProgressIndicator(stringResource(R.string.loading_user_database))
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(id = R.string.login),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                val uriHandler = LocalUriHandler.current
                if (!isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                            Text(
                                text = stringResource(R.string.no_account_question),
                                fontSize = 12.sp
                            )
                        }
                        TextButton(onClick = { uriHandler.openUri(Urls.signUpUrl) }) {
                            Text(
                                text = stringResource(id = R.string.sign_up),
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
            if (!isLoading) {
                TextButton(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onClick = onAuthorizationSkipped,
                ) {
                    Text(
                        text = stringResource(R.string.login_use_without_an_account),
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.overline,
                    )
                }
            }
        }
    }
}
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginWebView(
    onLoginSucceed: (String, String) -> Unit,
    onPageStarted: () -> Unit = {},
    onPageFinished: () -> Unit = {},
) {
    val codeVerifier by remember { mutableStateOf(Pkce.generateCodeVerifier()) }
    val url by remember {
        mutableStateOf(
            Urls.oauthBaseUrl +
                    "authorize?response_type=code&client_id=" +
                    BuildConfig.CLIENT_ID +
                    "&code_challenge=" +
                    codeVerifier
        )
    }
    val state = rememberWebViewState(
        url = url,
    )
    val client = remember {
        LoginWebViewClient(
            redirectCallback = onLoginSucceed,
            codeVerifier = codeVerifier,
            onPageStarted = onPageStarted,
            onPageFinished = onPageFinished,
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        WebView(
            modifier = Modifier.fillMaxSize(),
            state = state,
            client = client,
            onCreated = {
                it.settings.javaScriptEnabled = true
                android.webkit.WebView.setWebContentsDebuggingEnabled(false)
            }
        )

        val loadingState = state.loadingState
        if (loadingState is LoadingState.Loading) {
            LinearProgressIndicator(
                progress = loadingState.progress,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        true,
        false,
        false,
        MutableSharedFlow(),
    )
}

