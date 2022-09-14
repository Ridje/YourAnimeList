package com.kis.youranimelist.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.kis.youranimelist.core.utils.AppPreferences.Companion.ACCESS_TOKEN_SETTING_KEY
import com.kis.youranimelist.core.utils.AppPreferences.Companion.EXPIRES_IN_TOKEN_SETTING_KEY
import com.kis.youranimelist.core.utils.AppPreferences.Companion.NSFW_SETTING_KEY
import com.kis.youranimelist.core.utils.AppPreferences.Companion.ONBOARDING_SHOWN_PREF_KEY
import com.kis.youranimelist.core.utils.AppPreferences.Companion.REFRESH_TOKEN_SETTING_KEY
import com.kis.youranimelist.core.utils.AppPreferences.Companion.TYPE_TOKEN_SETTING_KEY

class AppPreferencesWrapper constructor(private val appPreferences: AppPreferences) {

    fun writeValue(key: Setting<String>, value: String) {
        appPreferences.writeString(key.key, value)
    }

    fun writeValue(key: Setting<Int>, value: Int) {
        appPreferences.writeInt(key.key, value)
    }

    fun writeValue(key: Setting<Boolean>, value: Boolean) {
        appPreferences.writeBoolean(key.key, value)
    }

    fun readValue(key: Setting<String>, defaultValue: String? = null): String {
        return appPreferences.readString(key.key, defaultValue)
    }

    fun readValue(key: Setting<Int>, defaultValue: Int? = null): Int {
        return appPreferences.readInt(key.key, defaultValue)
    }

    fun readValue(key: Setting<Boolean>, defaultValue: Boolean? = null): Boolean {
        return appPreferences.readBoolean(key.key, defaultValue)
    }

    fun removeSetting(key: Setting<*>) {
        appPreferences.removeSetting(key.key)
    }

    fun registerOnSharedPreferenceChangeListener(
        listener: OnSharedPreferencesChangeListenerWrapper<Boolean>,
        listenerKey: Setting<Boolean>,
    ) {
        appPreferences.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == listenerKey.key) {
                listener.onSharedPreferenceChanged(readValue(listenerKey))
            }
        }
    }
}

class AppPreferences(val context: Context) {

    private val encryptedSharedPreferences: SharedPreferences =
        EncryptedSharedPreferences.create(
            context.packageName,
            masterKeys,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)

    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        encryptedSharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun writeInt(key: String, value: Int) {
        val preferences = getPreferencesByKey(key)
        preferences.edit().putInt(key, value).apply()
    }

    fun writeString(key: String, value: String) {
        val preferences = getPreferencesByKey(key)
        preferences.edit().putString(key, value).apply()
    }

    fun writeBoolean(key: String, value: Boolean) {
        val preferences = getPreferencesByKey(key)
        preferences.edit().putBoolean(key, value).apply()
    }

    fun readBoolean(key: String, defaultValue: Boolean?): Boolean {
        val preferences = getPreferencesByKey(key)
        return preferences.getBoolean(key, defaultValue ?: false)
    }

    fun readInt(key: String, defaultValue: Int?): Int {
        val preferences = getPreferencesByKey(key)
        return preferences.getInt(key, defaultValue ?: 0)
    }

    fun readString(key: String, defaultValue: String?): String {
        return getPreferencesByKey(key).getString(key, defaultValue ?: "") ?: defaultValue ?: ""
    }

    fun removeSetting(key: String) {
        val preferences = getPreferencesByKey(key)
        preferences.edit().remove(key).apply()
    }

    private fun getPreferencesByKey(key: String): SharedPreferences {
        return if (encryptedPreferencesKeys.contains(key)) encryptedSharedPreferences
        else sharedPreferences
    }

    companion object {

        const val ACCESS_TOKEN_SETTING_KEY = "access_token"
        const val REFRESH_TOKEN_SETTING_KEY = "refresh_token"
        const val EXPIRES_IN_TOKEN_SETTING_KEY = "expires_in"
        const val TYPE_TOKEN_SETTING_KEY = "token_type"
        private val encryptedPreferencesKeys = listOf(ACCESS_TOKEN_SETTING_KEY,
            REFRESH_TOKEN_SETTING_KEY,
            EXPIRES_IN_TOKEN_SETTING_KEY,
            TYPE_TOKEN_SETTING_KEY)

        const val NSFW_SETTING_KEY = "nsfw"
        const val ONBOARDING_SHOWN_PREF_KEY = "onboarding_shown"

        private val masterKeys = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    }
}

interface OnSharedPreferencesChangeListenerWrapper<R> {
    fun onSharedPreferenceChanged(value: R)
}

sealed class Setting<T>(val key: String) {
    object AccessToken : Setting<String>(ACCESS_TOKEN_SETTING_KEY)
    object RefreshToken : Setting<String>(REFRESH_TOKEN_SETTING_KEY)
    object ExpiresInToken : Setting<Int>(EXPIRES_IN_TOKEN_SETTING_KEY)
    object TypeToken : Setting<String>(TYPE_TOKEN_SETTING_KEY)

    object NSFW : Setting<Boolean>(NSFW_SETTING_KEY)
    object OnboardingShown: Setting<Boolean>(ONBOARDING_SHOWN_PREF_KEY)
}

