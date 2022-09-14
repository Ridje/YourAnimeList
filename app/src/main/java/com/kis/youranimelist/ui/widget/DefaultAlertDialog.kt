package com.kis.youranimelist.ui.widget

import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kis.youranimelist.R

@Composable
fun DefaultAlertDialog(
    title: String,
    description: String,
    onDismissRequest: () -> Unit = {},
    onClickOk: () -> Unit = {},
) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Text(text = description)
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onClickOk) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        },
        backgroundColor = MaterialTheme.colors.background,
    )
}
