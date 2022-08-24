package com.kis.youranimelist.ui.widget

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TextProgressIndicator(text: String) {
    val coroutineScope = rememberCoroutineScope()
    var loadingText by remember { mutableStateOf(text) }
    LaunchedEffect(true) {
        coroutineScope.launch {
            val dotsList = listOf(
                ".",
                "..",
                "...",
            )
            var index = 0;
            while (true) {
                delay(500L)
                loadingText = text + dotsList[index % dotsList.size]
                index++
            }
        }
    }
    Text(text = loadingText)
}
