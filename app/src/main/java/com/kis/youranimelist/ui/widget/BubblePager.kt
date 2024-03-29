/*
 * MIT License
 *
 * Copyright (c) 2022 Vivek Singh
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.kis.youranimelist.ui.widget

import android.util.Log
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerDefaults
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.kis.youranimelist.R
import com.kis.youranimelist.ui.Theme
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BubblePager(
    pagerState: PagerState = rememberPagerState(),
    pageCount: Int,
    modifier: Modifier = Modifier,
    bubbleMinRadius: Dp = 48.dp,
    bubbleMaxRadius: Dp = 12000.dp,
    bubbleBottomPadding: Dp = 140.dp,
    bubbleColors: List<Color>,
    vector: ImageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right_solid),
    endVector: ImageVector = ImageVector.vectorResource(id = R.drawable.ic_thumbs_up_regular),
    onEndClick: () -> Unit = {},
    content: @Composable PagerScope.(Int) -> Unit,
) {
    val icon = rememberVectorPainter(vector)
    val endIcon = rememberVectorPainter(endVector)
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    val iconInteractionSource = remember { MutableInteractionSource() }
    var isNextClick by remember { mutableStateOf(false) }
    val arrowBubbleRadius by animateDpAsState(
        targetValue = if (pagerState.shouldHideBubble(isDragged,
                isNextClick)
        ) 0.dp else bubbleMinRadius,
        animationSpec = tween(Theme.NumberValues.animationBubbleMs)
    )
    val arrowIconSize by animateDpAsState(
        targetValue = if (pagerState.shouldHideBubble(isDragged,
                isNextClick)
        ) 0.dp else vector.defaultHeight,
        animationSpec = tween(Theme.NumberValues.animationBubbleMs)
    )
    val coroutineScope = rememberCoroutineScope()
    val iconColorFilter = remember { ColorFilter.tint(Color.White) }
    Box(modifier = modifier) {
        HorizontalPager(
            count = pageCount,
            state = pagerState,
            flingBehavior = bubblePagerFlingBehavior(pagerState),
            modifier = Modifier
                .drawBehind {
                    drawRect(color = bubbleColors[pagerState.currentPage], size = size)
                    val (radius, centerX) = calculateBubbleDimensions(
                        swipeProgress = pagerState.currentPageOffset,
                        easing = CubicBezierEasing(1f, 0f, .92f, .62f),
                        minRadius = bubbleMinRadius,
                        maxRadius = bubbleMaxRadius
                    )
                    drawBubble(
                        radius = radius,
                        centerX = centerX,
                        bottomPadding = bubbleBottomPadding,
                        color = pagerState.getBubbleColor(bubbleColors)
                    )
                    if (pagerState.currentPage < pageCount - 1) {
                        drawBubbleWithIcon(
                            radius = arrowBubbleRadius,
                            bottomPadding = bubbleBottomPadding,
                            color = pagerState.getNextBubbleColor(bubbleColors),
                            icon = icon,
                            iconSize = arrowIconSize,
                            iconColorFilter = iconColorFilter,
                        )
                    } else {
                        drawBubbleWithIcon(
                            radius = arrowBubbleRadius,
                            bottomPadding = bubbleBottomPadding,
                            color = pagerState.getNextBubbleColor(bubbleColors),
                            icon = endIcon,
                            iconSize = arrowIconSize,
                            iconColorFilter = iconColorFilter,
                        )
                    }
                }
        ) { page ->
            if (!isDragged) {
                val configuration = LocalConfiguration.current
                val density = LocalDensity.current
                val animateScrollNextSpec = remember<TweenSpec<Float>> {
                    tween(durationMillis = Theme.NumberValues.animationScrollMs)
                }
                Box(modifier = Modifier
                    .padding(bottom = bubbleBottomPadding - bubbleMinRadius)
                    .clip(shape = CircleShape)
                    .background(Color.Transparent)
                    .clickable(
                        interactionSource = iconInteractionSource,
                        indication = null
                    ) {
                        coroutineScope.launch {
                            if (pagerState.currentPage == pageCount - 1) {
                                launch {
                                    delay(Theme.NumberValues.animationScrollMs.div(2L))
                                    onEndClick.invoke()
                                }
                            }
                            isNextClick = true
                            launch {
                                delay(Theme.NumberValues.animationScrollMs.div(2L))
                                isNextClick = false
                            }
                            pagerState.animateScrollBy(
                                value = with(density) { configuration.screenWidthDp.dp.toPx() },
                                animationSpec = animateScrollNextSpec
                            )
                        }
                    }
                    .size(bubbleMinRadius.times(2))
                    .align(Alignment.BottomCenter)
                )
            }
            content(page)
        }
    }
}

fun DrawScope.drawBubbleWithIcon(
    radius: Dp,
    bottomPadding: Dp,
    color: Color,
    icon: VectorPainter,
    iconSize: Dp,
    iconColorFilter: ColorFilter,
) {
    translate(size.width / 2) {
        drawCircle(
            radius = radius.toPx(),
            color = color,
            center = Offset(0.dp.toPx(), size.height - bottomPadding.toPx())
        )
        with(icon) {
            iconSize.toPx().let { iconSize ->
                translate(
                    top = size.height - bottomPadding.toPx() - iconSize / 2,
                    left = -(iconSize / 2) + 8
                ) {
                    draw(size = Size(iconSize, iconSize), colorFilter = iconColorFilter)
                }
            }
        }
    }
}

fun DrawScope.drawBubble(
    radius: Dp,
    centerX: Dp,
    bottomPadding: Dp,
    color: Color,
) {
    translate(size.width / 2) {
        drawCircle(
            color = color,
            radius = radius.toPx(),
            center = Offset(centerX.toPx(), size.height - bottomPadding.toPx())
        )
    }
}

fun calculateBubbleDimensions(
    swipeProgress: Float,
    easing: Easing,
    minRadius: Dp,
    maxRadius: Dp,
): Pair<Dp, Dp> {
    val swipeValue = lerp(0f, 2f, swipeProgress.absoluteValue)
    val radius = lerp(
        minRadius,
        maxRadius,
        easing.transform(swipeValue)
    )
    var centerX = lerp(
        0.dp,
        maxRadius,
        easing.transform(swipeValue)
    )
    if (swipeProgress < 0) {
        centerX = -centerX
    }
    return Pair(radius, centerX)
}

@OptIn(ExperimentalPagerApi::class, ExperimentalSnapperApi::class)
@Composable
fun bubblePagerFlingBehavior(pagerState: PagerState) =
    PagerDefaults.flingBehavior(
        state = pagerState,
        snapAnimationSpec = spring(dampingRatio = 1.9f, stiffness = 600f),
    )

@OptIn(ExperimentalPagerApi::class)
fun PagerState.getBubbleColor(bubbleColors: List<Color>): Color {
    val index = if (currentPageOffset < 0) {
        currentPage - 1
    } else nextSwipeablePageIndex
    return bubbleColors[index]
}

@OptIn(ExperimentalPagerApi::class)
fun PagerState.getNextBubbleColor(bubbleColors: List<Color>): Color {
    return bubbleColors[nextSwipeablePageIndex]
}

@OptIn(ExperimentalPagerApi::class)
fun PagerState.shouldHideBubble(isDragged: Boolean, isPressed: Boolean): Boolean = derivedStateOf {
    Log.d("COMPOSE: ", isPressed.toString())
    var b = false
    if (isDragged || isPressed) {
        b = true
    }
    if (currentPageOffset.absoluteValue > Theme.NumberValues.hideBubbleThreshold || isPressed) {
        b = true
    }
    b
}.value

@OptIn(ExperimentalPagerApi::class)
val PagerState.nextSwipeablePageIndex: Int
    get() = if ((currentPage + 1) == pageCount) currentPage - 1 else currentPage + 1

fun lerp(start: Float, end: Float, value: Float): Float {
    return start + (end - start) * value
}

private val Theme.NumberValues.animationBubbleMs: Int
    get() = 350

private val Theme.NumberValues.hideBubbleThreshold: Double
    get() = 0.1

private val Theme.NumberValues.animationScrollMs: Int
    get() = 700


