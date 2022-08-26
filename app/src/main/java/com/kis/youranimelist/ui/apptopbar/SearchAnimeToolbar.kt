package com.kis.youranimelist.ui.apptopbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kis.youranimelist.R

@Composable
fun SearchAnimeToolbar(
    onSearchClick: () -> Unit = {},
) {

    TopAppBar(
        title = {
            Text(text = "Search in MAL", style = MaterialTheme.typography.subtitle1)
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(painter = painterResource(id = R.drawable.ic_search_solid),
                    contentDescription = stringResource(
                        id = R.string.default_content_description),
                    modifier = Modifier.height(18.dp)
                )
            }
        },
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onSearchClick.invoke() }
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
    )
}
