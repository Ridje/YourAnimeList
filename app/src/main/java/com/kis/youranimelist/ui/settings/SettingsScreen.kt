package com.kis.youranimelist.ui.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kis.youranimelist.R
import com.kis.youranimelist.core.utils.Urls
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.apptopbar.SettingsToolbar
import com.kis.youranimelist.ui.navigation.NavigationKeys
import com.kis.youranimelist.ui.widget.DefaultAlertDialog

@Composable
fun SettingsScreenRoute(
    navController: NavController,
    scaffoldState: ScaffoldState,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()
    val eventsListener = viewModel as SettingsScreenContract.ScreenEventsListener
    SettingsScreen(nsfw = screenState.value.nsfw,
        useAppAuth = screenState.value.useAppAuth,
        scaffoldState = scaffoldState,
        onNsfwCheckedChange = eventsListener::onNsfwChanged,
        onLogoutClick = eventsListener::onLogoutClicked,
        onConnectMalAccountClick = { navController.navigate("${NavigationKeys.Route.LOGIN}/true") }
    )
}


@Composable
fun SettingsScreen(
    nsfw: Boolean,
    useAppAuth: Boolean,
    scaffoldState: ScaffoldState,
    onNsfwCheckedChange: (Boolean) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onConnectMalAccountClick: () -> Unit = {},
) {
    Scaffold(scaffoldState = scaffoldState, topBar = { SettingsToolbar() }) {
        val showExitDialog = remember { mutableStateOf(false) }
        val showClearDataExitDialog = remember { mutableStateOf(false) }
        if (showExitDialog.value) {
            DefaultAlertDialog(
                title = stringResource(id = R.string.settings_logout),
                description = stringResource(id = R.string.settings_logout_dialog_description),
                onDismissRequest = { showExitDialog.value = false },
                onClickOk = {
                    showExitDialog.value = false
                    onLogoutClick.invoke()
                }
            )
        }
        if (showClearDataExitDialog.value) {
            DefaultAlertDialog(
                title = stringResource(id = R.string.settings_clear_data),
                description = stringResource(id = R.string.settings_clear_data_dialog_description),
                onDismissRequest = { showClearDataExitDialog.value = false },
                onClickOk = {
                    showClearDataExitDialog.value = false
                    onLogoutClick.invoke()
                }
            )
        }
        val uriHandler = LocalUriHandler.current
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .padding(bottom = Theme.NumberValues.bottomBarPaddingValueForLazyList.dp)
        ) {
            Category(title = stringResource(R.string.settings_category_api))
            SwitcherSetting(title = stringResource(R.string.settings_nsfw).uppercase(),
                description = stringResource(R.string.settings_nsfw_setting_description),
                value = nsfw,
                onValueChange = onNsfwCheckedChange,
                icon = R.drawable.ic_rated_r
            )
            SettingCategoryDivider()
            Category(title = stringResource(R.string.other))
            ClickableMenu(title = stringResource(R.string.settings_github),
                description = stringResource(R.string.settings_github_description),
                onClick = { uriHandler.openUri(Urls.appRedirectUrl) },
                icon = R.drawable.ic_github)
            ClickableMenu(title = stringResource(R.string.settings_contact_developer),
                description = stringResource(R.string.settings_contact_developer_description),
                onClick = { uriHandler.openUri(Urls.telegramDeveloperLink) },
                icon = R.drawable.ic_telegram)
            ClickableMenu(
                title = stringResource(R.string.settings_mal_abb),
                description = stringResource(R.string.settings_mal_abb_description),
                onClick = { uriHandler.openUri(Urls.malLink) },
                icon = R.drawable.ic_myanimelist,
            )
            SettingCategoryDivider()
            Category(title = stringResource(R.string.settings_category_profile))
            if (useAppAuth) {
                ClickableMenu(
                    title = stringResource(R.string.settings_connect_mal_account),
                    description = stringResource(R.string.settings_connect_mal_account_description),
                    onClick = onConnectMalAccountClick,
                    icon = R.drawable.ic_plug_solid,
                    important = true,
                )
                ClickableMenu(title = stringResource(R.string.settings_clear_data),
                    description = stringResource(R.string.settings_clear_data_description),
                    onClick = { showClearDataExitDialog.value = true },
                    icon = R.drawable.ic_sign_out
                )
            } else {
                ClickableMenu(title = stringResource(R.string.settings_logout),
                    description = stringResource(R.string.settings_logout_description),
                    onClick = { showExitDialog.value = true },
                    icon = R.drawable.ic_sign_out
                )
            }
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
    important: Boolean = false,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick.invoke() }
            .then(if (important) Modifier.background(MaterialTheme.colors.surface) else Modifier)
            .then(if (important) Modifier.border(0.dp,
                MaterialTheme.colors.primary,
                RoundedCornerShape(8.dp)) else Modifier)
            .then(if (important) Modifier.padding(vertical = 8.dp,
                horizontal = 8.dp) else Modifier.padding(vertical = 4.dp, horizontal = 8.dp))
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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



