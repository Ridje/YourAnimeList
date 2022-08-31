package com.kis.youranimelist.core.utils

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
