package com.kis.youranimelist.ui

import androidx.compose.ui.graphics.Color

object Theme {
    object Colors {
        val background = Color(0xFF0e1a2a)
        val onPrimary = Color.White
        val surface = Color(0xFF001129)
        val onSurface = Color(229, 236, 245, 255)
        val primary = Color(0xFFCF0C11)
        val secondary = Color(0xFFCF0C11)
        val secondaryVariant = Color(0xFFF30F15)
        val completedItemColor = Color(0xFF0D47A1)
        val watchingItemColor = Color(0xFF1B5E20)
        val onHoldItemColor = Color(0xFFF57F17)
        val droppedItemColor = Color(0xFFB71C1C)
        val planToWatchItemColor = Color(0xFFA2A2A2)
        val userScore = completedItemColor
    }

    object StringValues {
        const val separator = " | "
    }

    object NumberValues {
        const val defaultImageRatio = 0.7f
        const val almostOpaque = 0.99f
        const val secondaryColorNotActiveAlpha = 0.3f
        const val maxTitleLines = 3
        const val debounceDefaultDelay = 300L
        const val fullUppercaseMediaTypeCharsThreshold = 3
        const val bottomBarHeightDp = 56
        const val bottomBarPaddingValueForLazyList = bottomBarHeightDp + 6
    }
}
