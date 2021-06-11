package com.kis.youranimelist.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.kis.youranimelist.R
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferences private constructor(val context: Context) {

    private val encryptedSharedPreferences : SharedPreferences = EncryptedSharedPreferences.create(
        context.packageName,
        masterKeys,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)

    private fun getPreferencesByKey(key: String): SharedPreferences {
        return if (encryptedPreferencesKeys.contains(key)) encryptedSharedPreferences
        else sharedPreferences
    }

    fun writeInt(key : String, value : Int) {
        val preferences = getPreferencesByKey(key)
        preferences.edit().putInt(key, value).apply()
    }

    fun writeString(key: String, value: String) {
        val preferences = getPreferencesByKey(key)
        preferences.edit().putString(key, value).apply()
    }

    fun readString(key : String) : String {
        val preferences = getPreferencesByKey(key)
        return preferences.getString(key, "") ?: ""
    }

    fun removeSetting(key: String) {
        val preferences = getPreferencesByKey(key)
        preferences.edit().remove(key).apply()
    }

    fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener?) {
        encryptedSharedPreferences.registerOnSharedPreferenceChangeListener(
            listener
        )
        sharedPreferences.registerOnSharedPreferenceChangeListener(
            listener
        )
    }

    fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener?) {
        encryptedSharedPreferences.unregisterOnSharedPreferenceChangeListener(
            listener
        )
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(
            listener
        )
    }

    companion object {

        private var singleton: AppPreferences? = null

        const val ACCESS_TOKEN_SETTING_KEY = "access_token"
        const val REFRESH_TOKEN_SETTING_KEY = "refresh_token"
        const val EXPIRES_IN_TOKEN_SETTING_KEY = "expires_in"
        const val TYPE_TOKEN_SETTING_KEY = "token_type"
        private val encryptedPreferencesKeys = listOf(ACCESS_TOKEN_SETTING_KEY, REFRESH_TOKEN_SETTING_KEY, EXPIRES_IN_TOKEN_SETTING_KEY, TYPE_TOKEN_SETTING_KEY)

        const val NSFW_SETTING_KEY = "nsfw"

        private val masterKeys = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        fun getInstance(context: Context): AppPreferences {

            var instance = singleton
            instance ?: run {
                synchronized(this) {
                    singleton ?: run {
                        singleton = AppPreferences(context)
                        instance = singleton
                    }
                }
            }
            return instance!!
        }
    }

}

