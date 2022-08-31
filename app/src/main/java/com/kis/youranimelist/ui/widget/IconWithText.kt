package com.kis.youranimelist.ui.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kis.youranimelist.R

@Composable
fun IconWithText(
    text: String?,
    textStyle: TextStyle,
    @DrawableRes
    icon: Int,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    space: Dp = 8.dp,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = R.string.default_content_description),
            modifier = Modifier.size(textStyle.fontSize.value.dp),
            tint = tint,
        )
        Spacer(modifier = Modifier.width(space))
        Text(
            text = if (text?.isNotBlank() == true) {
                text
            } else {
                stringResource(id = R.string.not_rated)
            },
            style = textStyle,
        )
    }
}
