package com.kis.youranimelist.core.utils

import com.kis.youranimelist.ui.Theme.NumberValues.fullUppercaseMediaTypeCharsThreshold
import kotlinx.coroutines.CancellationException

inline fun <T, R> T.runCatchingWithCancellation(block: T.() -> R) {
    try {
        block()
    } catch (e: Throwable) {
        if (e is CancellationException) {
            throw e
        }
    }
}

fun String.uppercaseMediaType(): String {
    return if (this.length < fullUppercaseMediaTypeCharsThreshold) this.uppercase() else this.replaceFirstChar { it.uppercase() }
}
