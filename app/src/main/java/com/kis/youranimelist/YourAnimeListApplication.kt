package com.kis.youranimelist

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.kis.youranimelist.network.AuthInterceptor
import com.kis.youranimelist.utils.AppPreferences
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class YourAnimeListApplication : Application()
