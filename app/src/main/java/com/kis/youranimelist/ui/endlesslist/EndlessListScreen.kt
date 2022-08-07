package com.kis.youranimelist.ui.endlesslist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.kis.youranimelist.NavigationKeys
import com.kis.youranimelist.R
import com.kis.youranimelist.ui.widget.IconWithText

@Composable
fun EndlessListScreenRoute(
    navController: NavController,
    viewModel: EndlessListScreenViewModel = hiltViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()
    EndlessListScreen(
        listItems = screenState.value.items.collectAsLazyPagingItems(),
        { itemId: Int -> navController.navigate(NavigationKeys.Route.EXPLORE + "/$itemId") }
    )
}


@Composable
fun EndlessListScreen(
    listItems: LazyPagingItems<Item>,
    onItemClick: (Int) -> Unit,
) {
    if (listItems.loadState.refresh == LoadState.Loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(top = 20.dp,
            bottom = 0.dp,
            start = 6.dp,
            end = 6.dp),
            modifier = Modifier.fillMaxSize()) {
            items(listItems) { item ->
                Card(
                    modifier = Modifier
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            item?.id?.let(onItemClick)
                        }
                ) {
                    Row {
                        Box {
                            AsyncImage(
                                model = item?.imageUrl,
                                contentDescription = stringResource(id = R.string.default_content_description),
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .fillMaxHeight()
                                    .aspectRatio(0.7f)
                                    .clip(RoundedCornerShape(20.dp)),
                                contentScale = ContentScale.Crop,
                            )
                            Text(text = item?.rank.toString(),
                                style = MaterialTheme.typography.h3,
                                modifier = Modifier
                                    .align(
                                        Alignment.BottomEnd)
                                    .clip(RoundedCornerShape(topStartPercent = 40))
                                    .background(MaterialTheme.colors.background)
                                    .padding(horizontal = 6.dp)
                            )
                        }
                        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                            Text(
                                text = item?.title ?: "",
                                style = MaterialTheme.typography.h6,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = item?.genres ?: "",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.caption,
                                softWrap = false,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconWithText(
                                    text = item?.mean?.toString(),
                                    textStyle = MaterialTheme.typography.body1,
                                    icon = R.drawable.ic_star_solid,
                                    tint = Color.Yellow,
                                    space = 6.dp
                                )
                                Text(
                                    text = "${item?.mediaType?.uppercase() ?: ""}\u00A0(${item?.numEpisodes?.toString()})",
                                    style = MaterialTheme.typography.body1,
                                    softWrap = false,
                                )
                            }
                            Text(
                                text = item?.description
                                    ?: stringResource(id = R.string.default_content_description),
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(vertical = 8.dp),
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                }
                Divider(
                    color = Color.Transparent,
                    modifier = Modifier
                        .height(14.dp)
                )
            }
        }
    }
}

