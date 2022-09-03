package com.kis.youranimelist.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kis.youranimelist.R
import com.kis.youranimelist.core.utils.Urls.malProfile
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.navigation.NavigationKeys
import com.kis.youranimelist.ui.widget.IconWithText
import kotlinx.coroutines.launch
import me.bytebeats.views.charts.pie.PieChart
import me.bytebeats.views.charts.pie.PieChartData
import me.bytebeats.views.charts.pie.render.SimpleSliceDrawer

@Composable
fun ProfileScreenRoute(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState,
) {
    val data = viewModel.screenState.collectAsState()
    val listener = viewModel as ProfileScreenContract.ScreenEventsListener
    ProfileScreen(
        data.value.isLoading,
        data.value.isError,
        data.value.user?.pictureUrl,
        data.value.user?.backgroundUrl,
        data.value.user?.name,
        data.value.user?.location,
        data.value.user?.joinedAt,
        data.value.user?.birthday,
        data.value.statisticsPieData,
        data.value.legend,
        data.value.bottomStatisticsData,
        scaffoldState,
        {
            data.value.user?.pictureAnimeId?.let { animeId ->
                navController.navigate(NavigationKeys.Route.EXPLORE + "/${animeId}")
            }
        },
        onSnackbarPerformedAction = listener::onReloadClicked,
        onSnackbarDismissedAction = listener::onResetStateClicked,
        onLogoutClick = listener::onLogoutClick,
    )
}

@Composable
fun ProfileScreen(
    isLoading: Boolean,
    isError: Boolean,
    pictureURL: String?,
    backgroundUrl: String?,
    userName: String?,
    location: String?,
    joinedAt: String?,
    birthday: String?,
    statisticsPieData: PieChartData?,
    statisticsLegend: List<Pair<String, Color?>>?,
    bottomStatisticsData: ProfileScreenContract.BottomStatisticsData?,
    scaffoldState: ScaffoldState,
    onImageClick: () -> Unit,
    onSnackbarPerformedAction: () -> Unit,
    onSnackbarDismissedAction: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    val showExitDialog = remember { mutableStateOf(false) }
    if (isLoading) {
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
        }
    } else if (isError && userName.isNullOrBlank()) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        scope.launch {
            val snackResult = scaffoldState.snackbarHostState.showSnackbar(
                message = context.resources.getString(R.string.data_not_loaded_error),
                actionLabel = context.resources.getString(R.string.reload_data),
                duration = SnackbarDuration.Long
            )
            when (snackResult) {
                SnackbarResult.Dismissed -> onSnackbarDismissedAction.invoke()
                SnackbarResult.ActionPerformed -> onSnackbarPerformedAction.invoke()
            }
        }
    } else {
        val uriHandler = LocalUriHandler.current
        Column(modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = backgroundUrl,
                contentDescription = stringResource(id = R.string.default_content_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(Theme.NumberValues.profileBackgroundRatio)
                    .clickable { onImageClick.invoke() },
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .offset(y = Theme.NumberValues.avatarOffset)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                if (showExitDialog.value) {
                    AlertDialog(
                        title = {
                            Text(stringResource(R.string.logout))
                        },
                        text = {
                            Text(text = stringResource(R.string.logout_dialog_description))
                        },
                        onDismissRequest = { showExitDialog.value = false },
                        confirmButton = {
                            TextButton(onClick = {
                                showExitDialog.value = false
                                onLogoutClick.invoke()
                            }) {
                                Text(stringResource(R.string.ok))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showExitDialog.value = false
                            }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = pictureURL,
                        contentDescription = stringResource(id = R.string.default_content_description),
                        modifier = Modifier
                            .height(Theme.NumberValues.avatarHeight)
                            .clip(RoundedCornerShape(Theme.NumberValues.avatarCorner))
                            .background(MaterialTheme.colors.background)
                            .padding(start = 4.dp, end = 4.dp, top = 4.dp)
                            .clip(RoundedCornerShape(Theme.NumberValues.avatarCorner))
                            .widthIn(max = 300.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { userName?.let { uriHandler.openUri("$malProfile/$userName") } }
                        ) {
                            Text(
                                text = "${stringResource(id = R.string.link_to_user)}$userName",
                                style = MaterialTheme.typography.h6
                            )
                        }
                        IconButton(onClick = { showExitDialog.value = true },
                            modifier = Modifier.size(24.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_sign_out),
                                contentDescription = stringResource(R.string.default_content_description),
                                tint = MaterialTheme.colors.primary,
                            )
                        }
                    }
                    IconWithText(text = location,
                        textStyle = MaterialTheme.typography.body2,
                        icon = R.drawable.ic_location)
                    IconWithText(text = joinedAt,
                        textStyle = MaterialTheme.typography.body2,
                        icon = R.drawable.ic_clock)
                    IconWithText(text = birthday,
                        textStyle = MaterialTheme.typography.body2,
                        icon = R.drawable.ic_birthday)
                }
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Max),
                ) {
                    statisticsPieData?.let {
                        Box(modifier = Modifier.weight(1f)) {
                            PieChart(
                                pieChartData = statisticsPieData,
                                sliceDrawer = SimpleSliceDrawer(
                                    sliceThickness = 30f
                                ),
                                modifier = Modifier
                                    .aspectRatio(1f)
                            )
                            Text(
                                stringResource(id = R.string.anime),
                                style = MaterialTheme.typography.subtitle2,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(Theme.NumberValues.spacerWeight))
                    statisticsLegend?.let {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            verticalArrangement = Arrangement.SpaceAround,
                        ) {
                            for (legendPair in statisticsLegend) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    legendPair.second?.let { color ->
                                        Canvas(
                                            modifier = Modifier.size(10.dp)
                                        ) {
                                            drawOval(
                                                color = color
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text(
                                        text = legendPair.first,
                                        style = MaterialTheme.typography.subtitle2,
                                    )
                                }
                            }
                        }
                    }
                }
                bottomStatisticsData?.let { bottomStat ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = bottomStat.days ?: "",
                                style = MaterialTheme.typography.subtitle2)
                            Text(text = stringResource(id = R.string.days_spent),
                                style = MaterialTheme.typography.subtitle2)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = bottomStat.episodes ?: "",
                                style = MaterialTheme.typography.subtitle2)
                            Text(text = stringResource(id = R.string.episodes_watched),
                                style = MaterialTheme.typography.subtitle2)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = bottomStat.meanScore ?: "",
                                style = MaterialTheme.typography.subtitle2)
                            Text(text = stringResource(id = R.string.mean_score),
                                style = MaterialTheme.typography.subtitle2)
                        }
                    }
                }
            }
        }
    }
}

private val Theme.NumberValues.profileBackgroundRatio: Float
    get() = 1.55f

private val Theme.NumberValues.avatarOffset: Dp
    get() = (-80).dp

private val Theme.NumberValues.avatarHeight: Dp
    get() = 150.dp

private val Theme.NumberValues.avatarCorner: Int
    get() = 20

private val Theme.NumberValues.spacerWeight: Float
    get() = 0.3f


