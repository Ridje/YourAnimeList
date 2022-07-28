package com.kis.youranimelist.utils

import kotlin.random.Random

object Pkce {

    private const val CODE_VERIFIER_CHAR_POOL =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-._~"

    fun generateCodeVerifier() : String {
        return String.Companion.random()
    }

    private fun String.Companion.random(stringLength : Int = 120) : String {

        return (1..stringLength)
            .map { Random.nextInt(0, CODE_VERIFIER_CHAR_POOL.length) }
            .map(CODE_VERIFIER_CHAR_POOL::get)
            .joinToString("")
    }
}

