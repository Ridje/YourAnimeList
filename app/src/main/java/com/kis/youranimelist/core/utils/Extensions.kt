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

inline fun <T, R> T.returnCatchingWithCancellation(block: T.() -> R): Boolean {
    try {
        block()
        return true
    } catch (e: Throwable) {
        if (e is CancellationException) {
            throw e
        }
        return false

    }
}

fun String.uppercaseMediaType(): String {
    return if (this.length <= fullUppercaseMediaTypeCharsThreshold) this.uppercase() else this.replaceFirstChar { it.uppercase() }
}
