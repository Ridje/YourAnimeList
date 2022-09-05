package com.kis.youranimelist.ui.settings

object SettingsScreenContract {
    data class ScreenState(
        val nsfw: Boolean,
        val logged: Boolean,
    )

    interface ScreenEventsListener {
        fun onNsfwChanged(newValue: Boolean)
        fun onLogoutClicked()
    }
}
