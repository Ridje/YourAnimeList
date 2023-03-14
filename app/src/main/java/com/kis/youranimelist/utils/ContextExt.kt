package com.kis.youranimelist.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.FileProvider

fun Context.getFileProviderAuthorities(): String {
    val component = ComponentName(this, FileProvider::class.java)
    val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.packageManager.getProviderInfo(
            component, PackageManager.ComponentInfoFlags.of(
                PackageManager.GET_META_DATA.toLong()
            )
        )
    } else {
        this.packageManager.getProviderInfo(component, PackageManager.GET_META_DATA)
    }
    return info.authority
}
