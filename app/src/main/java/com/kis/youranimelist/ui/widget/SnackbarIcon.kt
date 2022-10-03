package com.kis.youranimelist.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kis.youranimelist.ui.Theme

@Composable
fun SnackbarIcon(
    snackbarHostState: SnackbarHostState,
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData: SnackbarData ->
            SnackbarLayout(message = snackbarData.message)
        }
    )
}

@Composable
private fun SnackbarLayout(message: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        modifier = Modifier
            .padding(16.dp)
            .height(60.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier
                .width(10.dp)
                .fillMaxHeight()
                .background(color = Theme.Colors.planToWatchItemColor)
            )
            Icon(imageVector = Icons.Default.Done,
                contentDescription = "",
                tint = Theme.Colors.planToWatchItemColor)
            Text(text = message)
        }
    }
}

@Preview
@Composable
fun SnackBarIconPreview() {
    SnackbarLayout(message = "Testing message")
}
