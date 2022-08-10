package com.kis.youranimelist.ui.item

import androidx.annotation.DrawableRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.kis.youranimelist.R
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.navigation.NavigationKeys
import com.kis.youranimelist.ui.widget.AnimeCategoryListItemRounded
import com.kis.youranimelist.ui.widget.ExpandableText

@Composable
fun ItemScreenRoute(
    navController: NavController,
    viewModel: ItemViewModel = hiltViewModel(),
) {
    val screeState = viewModel.screenState.collectAsState()
    ItemScreen(
        screeState.value.item,
        { navController.popBackStack() },
        { navController.popBackStack(NavigationKeys.Route.EXPLORE, false, false) },
        { animeId: Int -> navController.navigate(NavigationKeys.Route.EXPLORE + "/$animeId") },
    )
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ItemScreen(
    anime: AnimeItem,
    onBackButtonPressed: () -> Unit,
    onHomeButtonPressed: () -> Unit,
    onRelatedAnimeClicked: (Int) -> Unit,
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxHeight()
        .verticalScroll(rememberScrollState())
    ) {
        val (image, content) = createRefs()
        val pagerState = rememberPagerState()
        HorizontalPager(
            count = anime.images.size,
            modifier = Modifier.constrainAs(image) {
                top.linkTo(
                    parent.top
                )
            },
            state = pagerState
        ) { page ->
            AsyncImage(
                model =
                anime.images[page],
                contentDescription = stringResource(id = R.string.default_content_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .graphicsLayer { alpha = 0.99f }
                    .drawWithContent {
                        val colors = listOf(
                            Theme.Colors.background,
                            Theme.Colors.background,
                            Theme.Colors.background.copy(alpha = 0.99f),
                            Theme.Colors.background.copy(alpha = 0.1f),
                            Color.Transparent,
                        )
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(colors),
                            blendMode = BlendMode.DstIn
                        )
                    },
                contentScale = ContentScale.Crop,
            )
        }
        Row(modifier = Modifier.padding(20.dp)) {
            NavigateButton(
                onButtonPressed = onBackButtonPressed,
                iconRes = R.drawable.ic_arrow_left_solid,
            )
            Divider(
                modifier = Modifier.width(10.dp),
                color = Color.Transparent,
            )
            NavigateButton(
                onButtonPressed = onHomeButtonPressed,
                iconRes = R.drawable.ic_home_solid,
            )
        }
        Column(
            modifier = Modifier
                .constrainAs(content) {
                    top.linkTo(anchor = image.bottom, margin = (-150).dp)
                }
                .padding(24.dp),
        ) {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                activeColor = Color.Red.copy(0.8f),
                inactiveColor = Color.Red.copy(alpha = 0.3f),
            )
            Text(text = anime.title, style = MaterialTheme.typography.h5)
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = anime.year.toString(),
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .wrapContentWidth()
                )
                Text(text = " | ", style = MaterialTheme.typography.caption)
                Text(
                    text = anime.genres,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, false)
                )
                Text(text = " | ", style = MaterialTheme.typography.caption)
                Icon(
                    painter = painterResource(id = R.drawable.ic_star_solid),
                    contentDescription = "mean",
                    tint = Color.Yellow,
                    modifier = Modifier
                        .requiredHeightIn(max = 14.dp)
                        .padding(end = 4.dp),
                )
                Text(
                    text = anime.mean.toString(),
                    style = MaterialTheme.typography.caption,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            ExpandableText(text = anime.synopsis)
            RelatedItems(
                anime.relatedAnime,
                onRelatedAnimeClicked,
            )
        }
    }
}


@Composable
fun NavigateButton(
    onButtonPressed: () -> Unit,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
) {
    Button(onClick = onButtonPressed,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.primary
        ),
        modifier = modifier.size(40.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = stringResource(id = R.string.default_content_description),
            modifier = Modifier
                .width(20.dp)
                .aspectRatio(1f)
        )
    }
}

@Composable
fun RelatedItems(
    relatedAnimeItem: List<RelatedAnimeItem>,
    onItemClick: (Int) -> Unit,
) {
    Row(modifier = Modifier
        .horizontalScroll(rememberScrollState())
        .height(IntrinsicSize.Max)
    ) {
        for (relatedAnime in relatedAnimeItem) {
            AnimeCategoryListItemRounded(
                relatedAnime.picture,
                relatedAnime.title,
                relatedAnime.relationType,
                130.dp,
                3,
            ) { onItemClick.invoke(relatedAnime.id) }
            Divider(
                color = Color.Transparent,
                modifier = Modifier
                    .width(16.dp)
            )
        }
    }
}
