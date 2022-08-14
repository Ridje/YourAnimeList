package com.kis.youranimelist.ui.itembottomsheetdialog

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kis.youranimelist.R
import com.kis.youranimelist.domain.personalanimelist.model.AnimeStatusValue.Companion.dropNonValuableStatus
import com.kis.youranimelist.ui.login.LoginScreenContract
import com.kis.youranimelist.ui.widget.PostfixTransformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
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
        effectFlow = effectFlow,
        onScoreSliderValueChanged = eventsListener::onScoreChanged,
        onEpisodesWatchedValueChanged = eventsListener::onEpisodesWatchedChanged,
        onCancelClick = { navController.popBackStack() },
        onPlusOneClicked = eventsListener::onAdditionOneEpisodeWatched,
        onMinusOneClicked = eventsListener::onSubtractionOneEpisodeWatched,
        onStatusChanged = eventsListener::onStatusChanged,
        onApplyChanges = eventsListener::onApplyChanges,
        onDataSaved = { navController.popBackStack() }
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
    effectFlow: Flow<ItemBottomScreenContract.Effect>,
    onScoreSliderValueChanged: (Float) -> Unit,
    onEpisodesWatchedValueChanged: (Int?) -> Unit,
    onCancelClick: () -> Unit,
    onPlusOneClicked: () -> Unit,
    onMinusOneClicked: () -> Unit,
    onStatusChanged: (String) -> Unit,
    onApplyChanges: () -> Unit,
    onDataSaved: () -> Unit,
) {
    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is ItemBottomScreenContract.Effect.DataSaved -> onDataSaved.invoke()
                is ItemBottomScreenContract.Effect.DataSaveError -> {}
            }
        }
    }
    Column {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp),
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
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onCancelClick, Modifier.width(100.dp)) {
                    Text(text = "Cancel")
                }
                Button(onClick = { onApplyChanges.invoke() }, modifier = Modifier.width(100.dp)) {
                    if (applyLoading) {
                        CircularProgressIndicator(color = Color.White,
                            modifier = Modifier.size(20.dp), strokeWidth = 3.dp)
                    } else {
                        Text(text = "Apply")
                    }
                }
            }
            Box {
                var isStatusExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = isStatusExpanded,
                    onExpandedChange = { isStatusExpanded = !isStatusExpanded }) {
                    OutlinedTextField(
                        readOnly = true,
                        label = { Text(text = "Status") },
                        value = stringArrayResource(id = R.array.personal_list_statuses)[currentStatus],
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = isStatusExpanded,
                        onDismissRequest = { isStatusExpanded = false }
                    ) {
                        statuses.dropNonValuableStatus().forEach { status ->
                            DropdownMenuItem(
                                onClick = {
                                    onStatusChanged(status)
                                    isStatusExpanded = false
                                },
                            ) {
                                Text(
                                    text = stringArrayResource(id = R.array.personal_list_statuses)[statuses.indexOf(
                                        status)],
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
                    Text(text = "-1", style = MaterialTheme.typography.h6)
                }
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    value = episodesWatched?.let { episodesWatched.toString() } ?: "",
                    onValueChange = { value ->
                        onEpisodesWatchedValueChanged(value.takeIf { it.isNotBlank() }?.toInt())
                    },
                    isError = episodesWatched?.let { episodesWatched > episodes } ?: true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                    ),
                    label = { Text(text = "Episodes watched") },
                    visualTransformation = PostfixTransformation("/$episodes")
                )
                OutlinedButton(onClick = onPlusOneClicked,
                    modifier = Modifier.height(52.dp)
                ) {
                    Text(text = "+1", style = MaterialTheme.typography.h6)
                }
            }
            Column {
                Text(text = "Score: ${score.roundToInt()}")
                Slider(
                    value = score,
                    onValueChange = { value -> onScoreSliderValueChanged.invoke(value) },
                    valueRange = 0f..10f,
                    steps = 9,
                )
            }
        }
    }
}
