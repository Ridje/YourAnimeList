package com.kis.youranimelist.ui.apptopbar

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun SettingsToolbar(
) {
    TopAppBar(title = {
        Text(text = "Settings")
    })
}
