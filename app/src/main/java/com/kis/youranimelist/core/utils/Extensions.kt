package com.kis.youranimelist.core.utils

import com.kis.youranimelist.ui.Theme.NumberValues.fullUppercaseMediaTypeCharsThreshold
import kotlinx.coroutines.CancellationException

inline fun <T, R> T.returnCatchingWithCancellation(block: T.() -> R): R? {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        null
    }
}

inline fun <T, R> T.runCatchingWithCancellation(block: T.() -> R) {
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
    }
}

inline fun <T, R> T.returnFinishedCatchingWithCancellation(block: T.() -> R): Boolean {
    return try {
        block()
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        false
    }
}

fun String.uppercaseMediaType(): String {
    return if (this.length <= fullUppercaseMediaTypeCharsThreshold) this.uppercase() else this.replaceFirstChar { it.uppercase() }
}
