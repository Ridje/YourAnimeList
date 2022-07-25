package com.kis.youranimelist.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kis.youranimelist.model.app.Anime

@Composable
fun AnimeCategoryListItemRounded(anime: Anime) {
    Card(shape = RoundedCornerShape(20.dp), elevation = 6.dp) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(130.dp)
                .wrapContentHeight()) {
            AsyncImage(
                model = anime.mainPicture?.large,
                contentDescription = null,
                modifier = Modifier
                    .width(130.dp)
                    .aspectRatio(0.8f),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = anime.title,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(8.dp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}


@Composable
@Preview
fun AnimeCategoryListItemRoundedPreview() {
    AnimeCategoryListItemRounded(
        Anime(
            123,
            "Youkoso Jitsuryoku Shijou Shugi no Kyoushitsu e (TV) 2nd Season",
            null,
            null,
            null,
            null,
        )
    )
}
