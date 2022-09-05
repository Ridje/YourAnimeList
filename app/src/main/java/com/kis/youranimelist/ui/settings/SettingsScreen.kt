package com.kis.youranimelist.ui.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kis.youranimelist.R
import com.kis.youranimelist.core.utils.Urls
import com.kis.youranimelist.ui.apptopbar.SettingsToolbar

@Composable
fun SettingsScreenRoute(
    scaffoldState: ScaffoldState,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()
    val eventsListener = viewModel as SettingsScreenContract.ScreenEventsListener
    SettingsScreen(screenState.value.nsfw,
        scaffoldState,
        eventsListener::onNsfwChanged,
        eventsListener::onLogoutClicked
    )
}


@Composable
fun SettingsScreen(
    nsfw: Boolean,
    scaffoldState: ScaffoldState,
    onNsfwCheckedChange: (Boolean) -> Unit,
    onLogoutClick: () -> Unit,
) {
    Scaffold(scaffoldState = scaffoldState, topBar = { SettingsToolbar() }) {
        val showExitDialog = remember { mutableStateOf(false) }
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
        val uriHandler = LocalUriHandler.current
        Column(modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp)) {
            Category(title = stringResource(R.string.api))
            SwitcherSetting(title = stringResource(R.string.nsfw).uppercase(),
                description = stringResource(R.string.nsfw_setting_description),
                value = nsfw,
                onValueChange = onNsfwCheckedChange,
                icon = R.drawable.ic_rated_r
            )
            SettingCategoryDivider()
            Category(title = stringResource(R.string.other))
            ClickableMenu(title = stringResource(R.string.github).lowercase()
                .replaceFirstChar { it.uppercase() },
                description = stringResource(R.string.github_description),
                onClick = { uriHandler.openUri(Urls.appRedirectUrl) },
                icon = R.drawable.ic_github)
            ClickableMenu(title = stringResource(R.string.contact_developer).lowercase()
                .replaceFirstChar { it.uppercase() },
                description = stringResource(R.string.contact_developer_description),
                onClick = { uriHandler.openUri(Urls.telegramDeveloperLink) },
                icon = R.drawable.ic_telegram)
            ClickableMenu(
                title = stringResource(R.string.mal_abb).lowercase()
                    .replaceFirstChar { it.uppercase() },
                description = stringResource(R.string.mal_abb_description),
                onClick = { uriHandler.openUri(Urls.malLink) },
                icon = R.drawable.ic_myanimelist,
            )
            SettingCategoryDivider()
            Category(title = stringResource(R.string.profile))
            ClickableMenu(title = stringResource(R.string.logout).lowercase()
                .replaceFirstChar { it.uppercase() },
                description = stringResource(R.string.logout_description),
                onClick = { showExitDialog.value = true },
                icon = R.drawable.ic_sign_out
            )
        }
    }
}

@Composable
fun Category(
    title: String,
) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(text = title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 12.dp))
    }
}

@Composable
fun SwitcherSetting(
    title: String,
    description: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    @DrawableRes
    icon: Int,
) {
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)) {
        Icon(painter = painterResource(id = icon),
            contentDescription = stringResource(id = R.string.default_content_description),
            modifier = Modifier.width(32.dp))
        Column(modifier = Modifier
            .weight(1f, true)
            .padding(horizontal = 12.dp)) {
            Text(text = title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                Text(text = description,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
        }
        Switch(checked = value, onCheckedChange = onValueChange, colors = SwitchDefaults.colors(
            uncheckedThumbColor = MaterialTheme.colors.onSurface
        ))
    }
}

@Composable
fun SettingCategoryDivider() {
    Spacer(modifier = Modifier.height(12.dp))
    Divider(thickness = 2.dp)
}


@Composable
fun ClickableMenu(
    title: String,
    description: String,
    onClick: () -> Unit,
    @DrawableRes
    icon: Int,
) {
    Row(modifier = Modifier
        .clickable { onClick.invoke() }
        .padding(vertical = 4.dp, horizontal = 8.dp)
        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = R.string.default_content_description),
            modifier = Modifier.width(32.dp),
        )
        Column(modifier = Modifier
            .weight(1f, true)
            .padding(horizontal = 12.dp)) {
            Text(text = title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                Text(text = description,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}

