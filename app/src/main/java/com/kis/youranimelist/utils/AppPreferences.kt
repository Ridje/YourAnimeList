package com.kis.youranimelist.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class AppPreferences private constructor(private val context: Context) {

    private val sharedPreferences : SharedPreferences = EncryptedSharedPreferences.create(
        context.packageName,
        masterKeys,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    fun writeInt(key : String, value : Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun writeString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun readString(key : String) : String {
        return sharedPreferences.getString(key, "") ?: ""
    }

    fun removeSetting(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener?) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(
            listener
        )
    }

    fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener?) {
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

