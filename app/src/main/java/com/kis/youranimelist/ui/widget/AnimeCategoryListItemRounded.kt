package com.kis.youranimelist.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.kis.youranimelist.R
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.Theme.NumberValues.defaultImageRatio

@Composable
fun AnimeCategoryListItemRounded(
    cover: String?,
    firstLine: String,
    secondLine: String,
    size: Dp = 160.dp,
    maxLines: Int = 1,
    showPlaceholder: Boolean = false,
    showError: Boolean = false,
    onClick: () -> Unit = {},
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(size)
            .wrapContentHeight()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick.invoke() }
    ) {
        if (showPlaceholder) {
            Box(
                modifier = Modifier
                    .width(size)
                    .aspectRatio(defaultImageRatio)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color = Color.White.copy(alpha = 0.1f))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(size / Theme.NumberValues.widthLoaderRatioDivider),
                    color = Color.Red,
                )
            }

        } else if (showError) {
            Box(
                modifier = Modifier
                    .width(size)
                    .aspectRatio(defaultImageRatio)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color = Color.White.copy(alpha = 0.1f))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_exclamation_triangle_solid),
                    contentDescription = stringResource(id = R.string.default_content_description),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            SubcomposeAsyncImage(
                model = cover,
                contentDescription = null,
                modifier = Modifier
                    .width(size)
                    .aspectRatio(defaultImageRatio)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.width(size / Theme.NumberValues.widthLoaderRatioDivider)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center),
                            color = Color.Red,
                        )
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (showError) stringResource(R.string.error) else firstLine,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (showError) "" else secondLine.ifBlank { "" },
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private val Theme.NumberValues.widthLoaderRatioDivider
    get() = 4
