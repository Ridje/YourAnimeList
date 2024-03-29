package com.kis.youranimelist.ui.item

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Chip
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.kis.youranimelist.R
import com.kis.youranimelist.core.utils.uppercaseMediaType
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.Theme.NumberValues.maxTitleLines
import com.kis.youranimelist.ui.Theme.StringValues.separator
import com.kis.youranimelist.ui.navigation.NavigationKeys
import com.kis.youranimelist.ui.widget.AnimeCategoryListItemRounded
import com.kis.youranimelist.ui.widget.ExpandableText
import com.kis.youranimelist.ui.widget.SnackbarIcon
import com.kis.youranimelist.utils.ContextCompatUtils
import com.kis.youranimelist.utils.FileUtils
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ItemScreenRoute(
    navController: NavController,
    viewModel: ItemViewModel = hiltViewModel(),
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val effectFlow = viewModel.effectStream
    val listener = viewModel as ItemScreenContract.ScreenEventsListener
    ItemScreen(
        anime = screenState.item,
        effectFlow = effectFlow,
        listRelatedItems = screenState.listRelatedItems,
        listRecommendedItems = screenState.listRecommendedItems,
        onBackButtonPressed = { navController.popBackStack() },
        onHomeButtonPressed = {
            val currentRoute = navController.currentDestination?.route ?: ""
            val newRoute =
                navController.backQueue.last { it.destination.route != currentRoute }.destination.id
            navController.popBackStack(newRoute, false, false)
        },
        onRelatedAnimeClicked = { animeId: Int -> navController.navigate(NavigationKeys.Route.EXPLORE + "/$animeId") },
        onRecommendedAnimeClicked = { animeId: Int -> navController.navigate(NavigationKeys.Route.EXPLORE + "/$animeId") },
        onEditButtonPressed = { animeId: Int -> navController.navigate(NavigationKeys.Route.MY_LIST + "/$animeId") },
        onAddToBookmarksButtonPressed = listener::onAddToBookmarksButtonPressed,
    )
}


@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun ItemScreen(
    anime: ItemScreenContract.AnimeItem,
    effectFlow: SharedFlow<ItemScreenContract.Effect>,
    listRelatedItems: List<ItemScreenContract.RelatedAnimeItem>,
    listRecommendedItems: List<ItemScreenContract.RecommendedAnimeItem>,
    onBackButtonPressed: () -> Unit = {},
    onHomeButtonPressed: () -> Unit = {},
    onRelatedAnimeClicked: (Int) -> Unit = {},
    onRecommendedAnimeClicked: (Int) -> Unit = {},
    onEditButtonPressed: (Int) -> Unit = {},
    onAddToBookmarksButtonPressed: () -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarIcon(snackbarHostState = it)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            SnackbarShower(effectFlow = effectFlow, scaffoldState = scaffoldState)
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                val (image, content, blocks) = createRefs()
                val pagerState = rememberPagerState()
                var openDialog by remember { mutableStateOf(false) }
                if (openDialog) {
                    PicturesDialog(pagerState.currentPage, anime, { openDialog = false })
                }
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
                        model = anime.images[page],
                        contentDescription = stringResource(id = R.string.default_content_description),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(Theme.NumberValues.defaultImageRatio)
                            .graphicsLayer { alpha = Theme.NumberValues.almostOpaque }
                            .drawWithContent {
                                val colors = listOf(
                                    Theme.Colors.background,
                                    Theme.Colors.background,
                                    Theme.Colors.background.copy(alpha = Theme.NumberValues.itemMiddleFadeValue),
                                    Theme.Colors.background.copy(alpha = Theme.NumberValues.itemBottomFadeValue),
                                    Color.Transparent,
                                )
                                drawContent()
                                drawRect(
                                    brush = Brush.verticalGradient(colors),
                                    blendMode = BlendMode.DstIn
                                )
                            }
                            .clickable { openDialog = true },
                        contentScale = ContentScale.Crop,
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
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
                    if (anime.hasPersonalStatus) {
                        NavigateButton(
                            onButtonPressed = { onEditButtonPressed.invoke(anime.id) },
                            iconRes = R.drawable.ic_pen
                        )
                    } else {
                        NavigateButton(
                            onButtonPressed = onAddToBookmarksButtonPressed,
                            iconRes = R.drawable.ic_bookmark
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .constrainAs(content) {
                            top.linkTo(
                                anchor = image.bottom,
                                margin = Theme.NumberValues.contentShiftToImage
                            )
                        }
                        .padding(top = 24.dp, start = 24.dp, end = 24.dp),
                ) {
                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp),
                        activeColor = MaterialTheme.colors.secondary,
                        inactiveColor = MaterialTheme.colors.secondary.copy(alpha = Theme.NumberValues.secondaryColorNotActiveAlpha),
                    )
                    Text(text = anime.title, style = MaterialTheme.typography.h5)
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = anime.year.toString(),
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier
                                .wrapContentWidth()
                        )
                        Text(text = separator, style = MaterialTheme.typography.caption)
                        Text(
                            text = anime.mediaType.uppercaseMediaType(),
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier
                                .wrapContentWidth()
                        )
                        Text(text = separator, style = MaterialTheme.typography.caption)
                        Text(
                            text = "${anime.numEpisodes} ep.",
                            style = MaterialTheme.typography.caption,
                        )
                        Text(text = separator, style = MaterialTheme.typography.caption)
                        Text(
                            text = anime.airingStatus,
                            style = MaterialTheme.typography.caption,
                        )
                        Text(text = separator, style = MaterialTheme.typography.caption)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star_solid),
                            contentDescription = stringResource(id = R.string.default_content_description),
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
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (genre in anime.genres) {
                            Chip({}) {
                                Text(
                                    text = genre,
                                    style = MaterialTheme.typography.caption,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    ExpandableText(text = anime.synopsis)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Column(
                    modifier = Modifier.constrainAs(blocks) {
                        top.linkTo(content.bottom)
                    }
                ) {
                    RelatedItems(
                        listRelatedItems,
                        onRelatedAnimeClicked,
                    )
                    RecommendedItems(
                        recommendedAnimeItems = listRecommendedItems,
                        onItemClick = onRecommendedAnimeClicked
                    )
                }
            }
        }
    }

}


@Composable
fun NavigateButton(
    onButtonPressed: () -> Unit,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onButtonPressed,
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
    relatedAnimeItems: List<ItemScreenContract.RelatedAnimeItem>,
    onItemClick: (Int) -> Unit,
) {
    if (relatedAnimeItems.isNotEmpty()) {
        Column {
            Text(
                text = stringResource(R.string.related),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(PaddingValues(horizontal = 6.dp, vertical = 0.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                modifier = Modifier.height(270.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
            ) {
                items(relatedAnimeItems, key = { item -> item.id }) { relatedAnime ->
                    AnimeCategoryListItemRounded(
                        relatedAnime.picture,
                        relatedAnime.title,
                        relatedAnime.relationType,
                        130.dp,
                        maxTitleLines,
                    ) { onItemClick.invoke(relatedAnime.id) }
                    Divider(
                        color = Color.Transparent,
                        modifier = Modifier
                            .width(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendedItems(
    recommendedAnimeItems: List<ItemScreenContract.RecommendedAnimeItem>,
    onItemClick: (Int) -> Unit,
) {
    if (recommendedAnimeItems.isNotEmpty()) {
        Column {
            Text(
                text = stringResource(R.string.recommendations),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(PaddingValues(horizontal = 6.dp, vertical = 0.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                modifier = Modifier.height(270.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
            ) {
                items(recommendedAnimeItems, { it.id }) { recommendedAnime ->
                    AnimeCategoryListItemRounded(
                        recommendedAnime.picture,
                        recommendedAnime.title,
                        stringResource(
                            R.string.recommended_times,
                            recommendedAnime.recommendedTimes
                        ),
                        130.dp,
                        maxTitleLines,
                    ) { onItemClick.invoke(recommendedAnime.id) }
                    Divider(
                        color = Color.Transparent,
                        modifier = Modifier
                            .width(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PicturesDialog(
    initialPage: Int,
    anime: ItemScreenContract.AnimeItem,
    onDialogDismissed: () -> Unit,
) {
    val dialogPagerState = rememberPagerState(initialPage = initialPage)
    AlertDialog(
        onDismissRequest = onDialogDismissed,
        buttons = {
            HorizontalPager(
                modifier = Modifier
                    .wrapContentSize(),
                count = anime.images.size,
                state = dialogPagerState,
            ) { page ->
                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDialogDismissed
                        )
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Center)
                    ) {
                        var image: Drawable? by remember { mutableStateOf(null) }
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .align(Alignment.Center)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {}
                                ),
                            model = anime.images[page],
                            contentDescription = stringResource(id = R.string.default_content_description),
                            contentScale = ContentScale.FillWidth,
                            onSuccess = {
                                image = it.result.drawable
                            }
                        )
                        ShareButton(
                            image = image,
                            modifier = Modifier.align(Alignment.BottomEnd),
                            title = anime.title,
                        )
                    }
                }
            }
        },
        backgroundColor = Color.Transparent,
    )
}

private const val IMAGE_SHARE_TAG = "YAL.IMAGE_SHARE"
@Composable
fun ShareButton(
    image: Drawable?,
    modifier: Modifier = Modifier,
    title: String,
) {
    image?.let { sharedImage ->
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        NavigateButton(
            modifier = modifier.padding(8.dp),
            onButtonPressed = {
                scope.launch {
                    try {
                        val uriFile = FileUtils.createCachedShareFile(context, sharedImage)
                        ContextCompatUtils.startJPEGChooser(
                            context = context,
                            uri = uriFile,
                            title = title,
                        )
                    } catch (e: RuntimeException) {
                        Log.e(IMAGE_SHARE_TAG, "Can't share image now, short error: ${e.message}")
                        Toast.makeText(context, context.getString(R.string.сannot_share_image) + e.message, Toast.LENGTH_LONG).show()
                    }
                }
            },
            iconRes = R.drawable.ic_share,
        )
    }
}

@Composable
fun SnackbarShower(
    effectFlow: SharedFlow<ItemScreenContract.Effect>,
    scaffoldState: ScaffoldState,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is ItemScreenContract.Effect.ItemAddedToList -> {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = context.resources.getString(R.string.anime_added_plan_to_watch),
                            duration = SnackbarDuration.Short,
                        )
                    }
                }
            }
        }
    }
}


private val Theme.NumberValues.itemMiddleFadeValue: Float
    get() = Theme.NumberValues.almostOpaque


private val Theme.NumberValues.itemBottomFadeValue: Float
    get() = 0.1f

private val Theme.NumberValues.contentShiftToImage: Dp
    get() = (-150).dp
