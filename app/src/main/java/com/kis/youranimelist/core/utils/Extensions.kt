package com.kis.youranimelist.core.utils

import android.util.Log
import com.kis.youranimelist.ui.Theme.NumberValues.fullUppercaseMediaTypeCharsThreshold
import kotlinx.coroutines.CancellationException

inline fun <T, R> T.returnCatchingWithCancellation(block: T.() -> R): R? {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Log.d("Error happens during execution: ", e.stackTraceToString())
        null
    }
}

inline fun <T, R> T.runCatchingWithCancellation(block: T.() -> R) {
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Log.d("Error happens during execution: ", e.stackTraceToString())
    }
}

inline fun <T, R> T.returnFinishedCatchingWithCancellation(block: T.() -> R): Boolean {
    return try {
        block()
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Log.d("Error happens during execution: ", e.stackTraceToString())
        false
    }
}

fun String.uppercaseMediaType(): String {
    return if (this.length <= fullUppercaseMediaTypeCharsThreshold) this.uppercase() else this.replaceFirstChar { it.uppercase() }
}
