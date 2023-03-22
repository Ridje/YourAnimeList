package com.kis.youranimelist.ui.itembottomsheetdialog

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kis.youranimelist.R
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.widget.DefaultAlertDialog
import com.kis.youranimelist.ui.widget.PostfixTransformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ItemBottomScreenRoute(
    navController: NavController,
    viewModel: ItemBottomViewModel = hiltViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()
    val effectFlow = viewModel.effectStream
    val eventsListener = viewModel as ItemBottomScreenContract.ScreenEventsListener
    ItemBottomScreen(
        title = screenState.value.title,
        statuses = screenState.value.statuses,
        currentStatus = screenState.value.currentStatus,
        episodes = screenState.value.episodes,
        episodesWatched = screenState.value.episodesWatched,
        score = screenState.value.score,
        applyLoading = screenState.value.applyLoading,
        deleteLoading = screenState.value.deleteLoading,
        effectFlow = effectFlow,
        onScoreSliderValueChanged = eventsListener::onScoreChanged,
        onEpisodesWatchedValueChanged = eventsListener::onEpisodesWatchedChanged,
        onCancelClick = { navController.popBackStack() },
        onPlusOneClicked = eventsListener::onAdditionOneEpisodeWatched,
        onMinusOneClicked = eventsListener::onSubtractionOneEpisodeWatched,
        onStatusChanged = eventsListener::onStatusChanged,
        onApplyChanges = eventsListener::onApplyChanges,
        onDataSaved = { navController.popBackStack() },
        onDeleteEntryClick = eventsListener::onDeleteEntryClick,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemBottomScreen(
    title: String,
    statuses: List<String>,
    currentStatus: Int,
    episodes: Int,
    episodesWatched: Int?,
    score: Float,
    applyLoading: Boolean,
    deleteLoading: Boolean,
    effectFlow: Flow<ItemBottomScreenContract.Effect>,
    onScoreSliderValueChanged: (Float) -> Unit = {},
    onEpisodesWatchedValueChanged: (Int?) -> Unit = {},
    onCancelClick: () -> Unit = {},
    onPlusOneClicked: () -> Unit = {},
    onMinusOneClicked: () -> Unit = {},
    onStatusChanged: (String) -> Unit = {},
    onApplyChanges: () -> Unit = {},
    onDataSaved: () -> Unit = {},
    onDeleteEntryClick: () -> Unit = {},
) {
    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is ItemBottomScreenContract.Effect.DataSaved -> onDataSaved.invoke()
                is ItemBottomScreenContract.Effect.DataSaveError -> {}
            }
        }
    }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val animation: TweenSpec<Float> = remember {
        tween(
            Theme.NumberValues.scrollableTitleAnimeDuration,
            Theme.NumberValues.scrollableTitleAnimeStartDelay,
            easing = LinearEasing
        )
    }
    val showDialog = remember { mutableStateOf(false) }
    LaunchedEffect(title) {
        scope.launch {
            while (true) {
                scrollState.animateScrollTo(
                    scrollState.maxValue,
                    animationSpec = animation,
                )
                scrollState.animateScrollTo(
                    0,
                    animationSpec = animation,
                )
            }
        }
    }
    if (showDialog.value) {
        DefaultAlertDialog(
            title = stringResource(R.string.delete_anime_question),
            description = stringResource(R.string.delete_anime_question_description),
            onDismissRequest = { showDialog.value = false },
            onClickOk = {
                showDialog.value = false
                onDeleteEntryClick.invoke()
            }
        )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier
            .height(8.dp))
        Spacer(modifier = Modifier
            .width(30.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled))
        )
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .horizontalScroll(scrollState, false),
            )
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(MaterialTheme.colors.onSurface.copy(alpha = ButtonDefaults.OutlinedBorderOpacity))
            )
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box {
                var isStatusExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = isStatusExpanded,
                    onExpandedChange = { isStatusExpanded = !isStatusExpanded }) {
                    OutlinedTextField(
                        readOnly = true,
                        label = { Text(text = stringResource(R.string.status)) },
                        value = stringArrayResource(id = R.array.personal_list_statuses).drop(1)[currentStatus],
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = isStatusExpanded,
                        onDismissRequest = { isStatusExpanded = false }
                    ) {
                        statuses.forEach { status ->
                            DropdownMenuItem(
                                onClick = {
                                    onStatusChanged(status)
                                    isStatusExpanded = false
                                },
                            ) {
                                Text(
                                    text = stringArrayResource(id = R.array.personal_list_statuses)
                                        .drop(1)[statuses.indexOf(status)],
                                )
                            }
                        }
                    }
                }
            }
            Row(verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                OutlinedButton(onClick = onMinusOneClicked,
                    modifier = Modifier.height(52.dp)
                ) {
                    Text(text = stringResource(R.string.minus_one),
                        style = MaterialTheme.typography.h6)
                }
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    value = episodesWatched?.let { episodesWatched.toString() } ?: "",
                    onValueChange = { value ->
                        onEpisodesWatchedValueChanged(value.takeIf { it.isNotBlank() && it.isDigitsOnly() }?.toInt())
                    },
                    isError = episodesWatched?.let { episodesWatched > episodes } ?: true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        autoCorrect = false,
                    ),
                    keyboardActions = KeyboardActions {
                        onApplyChanges()
                    },
                    label = { Text(text = stringResource(id = R.string.episodes_watched)) },
                    visualTransformation = PostfixTransformation("/$episodes"),
                    singleLine = true,
                )
                OutlinedButton(onClick = onPlusOneClicked,
                    modifier = Modifier.height(52.dp)
                ) {
                    Text(text = stringResource(R.string.plus_one),
                        style = MaterialTheme.typography.h6)
                }
            }
            Column {
                Text(text = "${stringResource(id = R.string.score)}: ${score.roundToInt()}")
                Slider(
                    value = score,
                    onValueChange = { value -> onScoreSliderValueChanged.invoke(value) },
                    valueRange = 0f..10f,
                    steps = 9,
                )
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { showDialog.value = true },
                    modifier = Modifier.height(40.dp),
                    enabled = !deleteLoading && !applyLoading
                ) {
                    if (deleteLoading) {
                        CircularProgressIndicator(color = contentColorFor(MaterialTheme.colors.primary),
                            modifier = Modifier.size(20.dp), strokeWidth = 3.dp)
                    } else {
                        Icon(painterResource(id = R.drawable.ic_trash),
                            contentDescription = stringResource(
                                id = R.string.default_content_description),
                            tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onCancelClick,
                        modifier = Modifier
                            .width(100.dp)
                            .height(40.dp)) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = { onApplyChanges.invoke() },
                        modifier = Modifier
                            .width(100.dp)
                            .height(40.dp),
                        enabled = !deleteLoading && !applyLoading,
                    ) {
                        if (applyLoading) {
                            CircularProgressIndicator(color = contentColorFor(MaterialTheme.colors.primary),
                                modifier = Modifier.size(20.dp), strokeWidth = 3.dp)
                        } else {
                            Text(text = stringResource(R.string.apply))
                        }
                    }
                }
            }
        }
    }
}

private val Theme.NumberValues.scrollableTitleAnimeDuration: Int
    get() = 4000

private val Theme.NumberValues.scrollableTitleAnimeStartDelay: Int
    get() = 200
