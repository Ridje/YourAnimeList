package com.kis.youranimelist.core.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.kis.youranimelist.R
import com.kis.youranimelist.core.utils.AppPreferences.Companion.ACCESS_TOKEN_SETTING_KEY
import com.kis.youranimelist.core.utils.AppPreferences.Companion.EXPIRES_IN_TOKEN_SETTING_KEY
import com.kis.youranimelist.core.utils.AppPreferences.Companion.NSFW_SETTING_KEY
import com.kis.youranimelist.core.utils.AppPreferences.Companion.ONBOARDING_SHOWN_PREF_KEY
import com.kis.youranimelist.core.utils.AppPreferences.Companion.PERSONAL_LIST_SORT
import com.kis.youranimelist.core.utils.AppPreferences.Companion.REFRESH_TOKEN_SETTING_KEY
import com.kis.youranimelist.core.utils.AppPreferences.Companion.TYPE_TOKEN_SETTING_KEY
import com.kis.youranimelist.core.utils.AppPreferences.Companion.USE_APP_AUTH
import com.kis.youranimelist.ui.mylist.MyListScreenContract

private const val PREFERENCES_TAG = "AppPreferencesWrapper"
class AppPreferencesWrapper constructor(
    val appPreferences: AppPreferences,
) {

    fun logError(message: String) {
        Log.e(PREFERENCES_TAG, message)
    }

    fun writeValue(key: Setting<String>, value: String) {
        try {
            appPreferences.writeString(key.key, value)
        } catch (e: Exception) {
            logError("Event.WriteStringValue:" +
                    " Can't write key {$key} with value {$value}, error: ${e.message}")
        }
    }

    fun writeValue(key: Setting<Int>, value: Int) {
        try {
            appPreferences.writeInt(key.key, value)
        } catch (e: Exception) {
            logError("Event.WriteIntValue:" +
                    " Can't write key {$key} with value {$value}, error: ${e.message}")
        }
    }

    fun writeValue(key: Setting<Boolean>, value: Boolean) {
        try {
            appPreferences.writeBoolean(key.key, value)
        } catch (e: Exception) {
            logError("Event.WriteBooleanValue:" +
                    " Can't write key {$key} with value {$value}, error: ${e.message}")
        }
    }

    fun <T: Enum<T>> writeValue(key: Setting<T>, value: T) {
        try {
            appPreferences.writeInt(key.key, value.ordinal)
        } catch (e: Exception) {
            logError("Event.WriteEnumValue:" +
                    " Can't write key {$key} with value {$value}, error: ${e.message}")
        }
    }

    fun readValue(key: Setting<String>, defaultValue: String? = null): String {
        return try {
             appPreferences.readString(key.key, defaultValue)
        } catch (e: Exception) {
            logError("Event.ReadStringValue: Can't read key {$key}, error: ${e.message}")
            defaultValue ?: ""
        }
    }

    fun readValue(key: Setting<Int>, defaultValue: Int? = null): Int {
        return try {
            appPreferences.readInt(key.key, defaultValue)
        } catch (e: Exception) {
            logError("Event.ReadIntValue: Can't read key {$key}, error: ${e.message}")
            defaultValue ?: 0
        }
    }

    inline fun <reified T : Enum<T>> readValue(key: Setting<T>, defaultValue: T? = null): T {
        return try {
            val defaultIndexValue = enumValues<T>().indexOf(defaultValue)
            val ordinal = appPreferences.readInt(key.key, defaultIndexValue)
            enumValues<T>()[ordinal]
        } catch (e: Exception) {
            logError("Event.ReadEnumValue: Can't read key {$key}, error: ${e.message}")
            defaultValue ?: enumValues<T>().first()
        }
    }

    fun readValue(key: Setting<Boolean>, defaultValue: Boolean? = null): Boolean {
        return try {
            appPreferences.readBoolean(key.key, defaultValue)
        } catch (e: Exception) {
            logError("Event.ReadBooleanValue: Can't read key {$key}, error: ${e.message}")
            defaultValue ?: false
        }
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
        private val encryptedPreferencesKeys = listOf(
            ACCESS_TOKEN_SETTING_KEY,
            REFRESH_TOKEN_SETTING_KEY,
            EXPIRES_IN_TOKEN_SETTING_KEY,
            TYPE_TOKEN_SETTING_KEY
        )

        const val NSFW_SETTING_KEY = "nsfw"
        const val ONBOARDING_SHOWN_PREF_KEY = "onboarding_shown"
        const val USE_APP_AUTH = "use_app_auth"
        const val PERSONAL_LIST_SORT = "personal_list_sort"

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
    object PersonalListSort: Setting<SortType>(PERSONAL_LIST_SORT)
    object UseAppAuth: Setting<Boolean>(USE_APP_AUTH)
}

enum class SortType(val comparator: Comparator<MyListScreenContract.Item>) {
    Title(compareBy({ it.status }, { it.title })),
    Score(compareBy<MyListScreenContract.Item> { it.status }.thenByDescending { it.score }),
    Updated(compareBy<MyListScreenContract.Item> { it.status }.thenByDescending { it.updatedAt });

    companion object Factory {
        fun getTitleResource(sortType: SortType): Int {
            return when (sortType) {
                Score -> R.string.sort_score
                Title -> R.string.sort_title
                Updated -> R.string.sort_updated
            }
        }
    }
}
