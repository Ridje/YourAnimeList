package com.kis.youranimelist.ui.navigation

class InvalidNavArgumentException(
    expectedArgumentName: String,
) : Exception("Expected $expectedArgumentName but received no argument in navigation command")
