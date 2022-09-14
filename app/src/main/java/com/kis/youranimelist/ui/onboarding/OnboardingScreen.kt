package com.kis.youranimelist.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.kis.youranimelist.R
import com.kis.youranimelist.ui.Theme
import com.kis.youranimelist.ui.widget.BubblePager

@Composable
fun OnboardingScreenRoute(
    navController: NavController,
    viewModel: OnboardingScreenViewModel = hiltViewModel(),
) {
    val state = viewModel.screenState.collectAsState()
    OnboardingScreen(
        cards = state.value.onboardingCards,
        onSkipClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    cards: List<OnboardingScreenContract.OnboardingItem>,
    onSkipClick: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        BubblePager(
            pageCount = cards.size,
            bubbleColors = cards.map { it.backgroundColor },
            modifier = Modifier.fillMaxSize(),
        ) { index ->
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(modifier = Modifier
                    .fillMaxHeight(Theme.NumberValues.screenDescriptionPartOfScreen)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 40.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = cards[index].icon),
                        contentDescription = stringResource(id = R.string.default_content_description),
                        modifier = Modifier
                            .size(180.dp)
                            .padding(horizontal = 40.dp)
                            .aspectRatio(1f),
                        colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.8f)),
                    )
                    Text(
                        text = stringResource(id = cards[index].sign),
                        style = MaterialTheme.typography.h5,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }

                TextButton(
                    onClick = onSkipClick,
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(text = if (index == cards.size - 1) {
                            stringResource(R.string.finish)
                        } else {
                            stringResource(R.string.skip)
                        },
                            color = Color.White,
                            style = MaterialTheme.typography.h5,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
        }
    }
}

private val Theme.NumberValues.screenDescriptionPartOfScreen: Float
    get() = 0.7f

@Composable
fun OnboardingScreenPreview() {
    val screenZero = OnboardingScreenContract.OnboardingItem(
        backgroundColor = Theme.Colors.background,
        sign = R.string.onboarding_welcome,
        icon = R.drawable.ic_child_solid
    )
    val screenOne = OnboardingScreenContract.OnboardingItem(
        backgroundColor = Theme.Colors.secondary,
        sign = R.string.onboarding_explore,
        icon = R.drawable.ic_explore,
    )
    val screenTwo = OnboardingScreenContract.OnboardingItem(
        backgroundColor = Theme.Colors.background,
        sign = R.string.onboarding_personal_list,
        icon = R.drawable.ic_list_ol
    )
    val screenThree = OnboardingScreenContract.OnboardingItem(
        backgroundColor = Theme.Colors.secondary,
        sign = R.string.onboarding_profile,
        icon = R.drawable.ic_user,
    )
    val screenFour = OnboardingScreenContract.OnboardingItem(
        backgroundColor = Theme.Colors.background,
        sign = R.string.onboarding_settings,
        icon = R.drawable.ic_settings
    )
    OnboardingScreen(
        listOf(
            screenZero,
            screenOne,
            screenTwo,
            screenThree,
            screenFour,
        )
    )
}
