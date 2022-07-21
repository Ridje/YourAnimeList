package com.kis.youranimelist.ui.login

import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.google.accompanist.web.AccompanistWebViewClient
import com.kis.youranimelist.repository.RepositoryNetwork
import com.kis.youranimelist.utils.Urls

class LoginWebViewComponent(
    private val redirectCallback: (String, String) -> Unit,
    private val codeVerifier: String,
) : AccompanistWebViewClient() {

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?,
    ): Boolean {
        request?.let {
            if ((it.url.host + request.url.path) == Urls.appRedirectUrl) {
                it.url.getQueryParameter(RepositoryNetwork.CODE_FIELD)?.let { token ->
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
