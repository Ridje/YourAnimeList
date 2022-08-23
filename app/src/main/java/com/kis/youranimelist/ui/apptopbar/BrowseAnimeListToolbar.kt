package com.kis.youranimelist.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kis.youranimelist.R

@Composable
fun BrowseAnimeListToolbar(
    onNavigationIconClick: () -> Unit,
    title: String,
) {
    TopAppBar(
        title = { Text(text = title) },
        backgroundColor = Color.Transparent,
        modifier = Modifier.background(Color.Transparent),
        elevation = 0.dp,
        navigationIcon = {
            IconButton(
                onClick = { onNavigationIconClick.invoke() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left_solid),
                    contentDescription = stringResource(id = R.string.default_content_description),
                    modifier = Modifier
                        .width(20.dp)
                        .aspectRatio(1f)
                )
            }
        },
    )
}

