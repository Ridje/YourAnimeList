package com.kis.youranimelist.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun AnimeCategoryListItemRounded(
    cover: String?,
    firstLine: String,
    secondLine: String,
    size: Dp = 160.dp,
    maxLines: Int = 1,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(size)
            .wrapContentHeight()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick.invoke() }
    ) {
        AsyncImage(
            model = cover,
            contentDescription = null,
            modifier = Modifier
                .width(size)
                .aspectRatio(0.7f)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = firstLine,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = secondLine,
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
@Preview
fun AnimeCategoryListItemRoundedPreview() {
    AnimeCategoryListItemRounded(
        null,
        "Youkoso Jitsuryoku Shijou Shugi no Kyoushitsu e (TV) 2nd Season",
        "1996 spring",
    )
    {}
}
