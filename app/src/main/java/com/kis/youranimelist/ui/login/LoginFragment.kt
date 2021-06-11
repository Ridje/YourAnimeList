package com.kis.youranimelist.ui.login

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kis.youranimelist.BuildConfig
import com.kis.youranimelist.databinding.FragmentLoginBinding
import com.kis.youranimelist.navigateToDefaultFragment
import com.kis.youranimelist.repository.RepositoryNetwork
import com.kis.youranimelist.utils.Pkce
import com.kis.youranimelist.utils.AppPreferences
import com.kis.youranimelist.utils.Urls
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {


    @Inject
    lateinit var repositoryNetwork : RepositoryNetwork
    private var _binding: FragmentLoginBinding? = null
    private val codeVerifier = Pkce.generateCodeVerifier()
    private val codeChallenge = codeVerifier

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.settings.javaScriptEnabled = true

        binding.loginButton.setOnClickListener { openURL() }
        binding.webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                request?.let {
                    if ((it.url.host + request.url.path) == Urls.appRedirectUrl) {
                        it.url.getQueryParameter(RepositoryNetwork.CODE_FIELD)?.let {
                            lifecycleScope.launchWhenCreated {

                                val postResult = withContext(Dispatchers.IO) {
                                    repositoryNetwork.getAccessToken(BuildConfig.CLIENT_ID, it, codeVerifier, "authorization_code")
                                }

                                activity?.let { fragmentActivity ->
                                    val appPreferencesInstance = AppPreferences.getInstance(fragmentActivity.applicationContext)
                                    appPreferencesInstance.writeString(AppPreferences.ACCESS_TOKEN_SETTING_KEY, postResult.acessToken)
                                    appPreferencesInstance.writeString(AppPreferences.REFRESH_TOKEN_SETTING_KEY, postResult.refreshToken)
                                    appPreferencesInstance.writeInt(AppPreferences.EXPIRES_IN_TOKEN_SETTING_KEY, postResult.expiresIn)
                                    appPreferencesInstance.writeString(AppPreferences.TYPE_TOKEN_SETTING_KEY, postResult.tokenType)
                                    fragmentActivity.navigateToDefaultFragment()
                                }
                            }
                        }
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }


        }
    }

    private fun openURL() {
        val loginUrl =
            Uri.parse("${Urls.oauthBaseUrl}authorize?response_type=code&client_id=${BuildConfig.CLIENT_ID}&code_challenge=$codeChallenge")
        binding.webView.loadUrl(loginUrl.toString())
        binding.contentLayout.visibility = View.GONE
        binding.webView.visibility = View.VISIBLE

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}