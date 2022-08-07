package com.kis.youranimelist.ui.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kis.youranimelist.R

@Composable
fun ExpandableText(
    text: String,
) {
    if (text.isNotBlank()) {
        var isExpanded by remember { mutableStateOf(value = false) }
        val onClick = { isExpanded = !isExpanded }
        var hasOverflow by remember { mutableStateOf(false) }
        var lineCount by remember { mutableStateOf(0) }
        val maxLines by remember { mutableStateOf(3) }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() })) {
            Box {
                Text(
                    text = text, style = MaterialTheme.typography.body2,
                    maxLines = if (isExpanded) Int.MAX_VALUE else maxLines,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .animateContentSize(),
                    onTextLayout = { textLayoutResult: TextLayoutResult ->
                        hasOverflow = textLayoutResult.hasVisualOverflow
                        lineCount = textLayoutResult.lineCount
                    },
                )
                if (!isExpanded && hasOverflow) {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.typography.body2.fontSize.value.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colors.background
                                    )
                                )
                            )
                            .align(Alignment.BottomCenter)
                    )
                }
            }
            if (hasOverflow) {
                Icon(painter = painterResource(R.drawable.ic_chevron_down_solid),
                    contentDescription = stringResource(id = R.string.default_content_description),
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.requiredHeightIn(max = 14.dp)
                )
            } else if (lineCount > maxLines) {
                Icon(painter = painterResource(R.drawable.ic_chevron_up_solid),
                    contentDescription = stringResource(id = R.string.default_content_description),
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.requiredHeightIn(max = 14.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
@Preview
fun ExpandableTextPreview(
) {
    ExpandableText(text = "LALALA")
}
