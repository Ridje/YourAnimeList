package com.kis.youranimelist.ui.login

import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.google.accompanist.web.AccompanistWebViewClient
import com.kis.youranimelist.core.utils.Urls

private const val CODE_FIELD = "code"

class LoginWebViewClient(
    private val redirectCallback: (String, String) -> Unit,
    private val codeVerifier: String,
) : AccompanistWebViewClient() {

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?,
    ): Boolean {
        request?.let {
            if ((it.url.host + request.url.path) == Urls.appRedirectUrl) {
                it.url.getQueryParameter(CODE_FIELD)?.let { token ->
                    redirectCallback.invoke(
                        token,
                        codeVerifier,
                    )
                }
                return true;
            }
        }
        return super.shouldOverrideUrlLoading(view, request)
    }
}
