package com.kis.youranimelist

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.kis.youranimelist.repository.RepositoryNetwork
import com.kis.youranimelist.utils.AppPreferences

class YourAnimeListApplication : Application(), OnSharedPreferenceChangeListener {

    override fun onCreate() {
        super.onCreate()
        AppPreferences.getInstance(applicationContext).registerOnSharedPreferenceChangeListener(this)
        loadAppPreferences(applicationContext)
    }

    companion object {

        lateinit var accessToken : String private set
        lateinit var refreshToken : String private set
        lateinit var accessTokenType : String private set

        fun loadAppPreferences(applicationContext: Context) {
            with(AppPreferences) {
                val prefInstance = getInstance(applicationContext)
                accessToken = prefInstance.readString(ACCESS_TOKEN_SETTING_KEY)
                refreshToken = prefInstance.readString(REFRESH_TOKEN_SETTING_KEY)
                accessTokenType = prefInstance.readString(TYPE_TOKEN_SETTING_KEY)
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        loadAppPreferences(applicationContext)
        RepositoryNetwork.rebuildServices()
    }
}