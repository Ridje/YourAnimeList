package com.kis.youranimelist.data.network

import android.util.Log
import com.kis.youranimelist.core.utils.AppPreferencesWrapper
import com.kis.youranimelist.core.utils.OnSharedPreferencesChangeListenerWrapper
import com.kis.youranimelist.core.utils.Setting
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

private const val NSFW_FIELD_NAME = "nsfw"
class NSFWInterceptor @Inject constructor(
    appPreferencesWrapper: AppPreferencesWrapper,
) : Interceptor, OnSharedPreferencesChangeListenerWrapper<Boolean> {

    var nsfw: AtomicBoolean = AtomicBoolean(false)

    init {
        appPreferencesWrapper.registerOnSharedPreferenceChangeListener(
            this,
            Setting.NSFW
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (nsfw.get()) {
            Log.d("NSFWInterceptor", "NSFW api setting is turned on: replacing request query")
            val builtUrl = request.url.newBuilder().addQueryParameter(NSFW_FIELD_NAME, nsfw.get().toString()).build()
            request = request.newBuilder().url(builtUrl).build()
        }
        return chain.proceed(request)
    }

    override fun onSharedPreferenceChanged(value: Boolean) {
        nsfw.set(value)
        Log.d("NSWFInterceptor", "NSFW: value = $nsfw")
    }

}
