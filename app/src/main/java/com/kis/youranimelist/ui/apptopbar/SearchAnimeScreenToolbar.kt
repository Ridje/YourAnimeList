package com.kis.youranimelist.ui.apptopbar

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.kis.youranimelist.R

@Composable
fun SearchAnimeScreenToolbar(
    searchValue: String,
    requestSearchFocus: Boolean,
    onNavigationIconClick: () -> Unit = {},
    onSearchClick: (String) -> Boolean,
    onSearchValueChanged: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        if (requestSearchFocus) {
            focusRequester.requestFocus()
        }
    }
    Surface(modifier = Modifier
        .padding(vertical = 8.dp)
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.1f)
    ) {
        TextField(
            value = searchValue,
            onValueChange = { newSearchValue ->
                onSearchValueChanged.invoke(newSearchValue)
            },
            placeholder = {
                Text(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    text = "Search in MAL",
                    color = Color.White
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.White.copy(alpha = ContentAlpha.medium),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                leadingIconColor = contentColorFor(backgroundColor),
                trailingIconColor = contentColorFor(backgroundColor)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (onSearchClick.invoke(searchValue)) {
                        focusManager.clearFocus()
                    }
                }
            ),
            leadingIcon = {
                IconButton(
                    onClick = {
                        focusManager.clearFocus()
                        onNavigationIconClick.invoke()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left_solid),
                        contentDescription = stringResource(id = R.string.default_content_description),
                        modifier = Modifier
                            .width(20.dp)
                            .aspectRatio(1f)
                    )
                }
            },
            trailingIcon = if (searchValue.isEmpty()) {
                {}
            } else {
                {
                    IconButton(
                        onClick = {
                            onSearchValueChanged.invoke("")
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cancel),
                            contentDescription = stringResource(id = R.string.default_content_description),
                            modifier = Modifier
                                .width(20.dp)
                                .aspectRatio(1f)
                        )
                    }
                }
            },
            modifier = Modifier.focusRequester(focusRequester)
        )
    }
}
