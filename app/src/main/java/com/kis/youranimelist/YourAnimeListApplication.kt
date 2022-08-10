package com.kis.youranimelist

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.kis.youranimelist.data.network.AuthInterceptor
import com.kis.youranimelist.core.utils.AppPreferences
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class YourAnimeListApplication : Application()
