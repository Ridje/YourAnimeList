package com.kis.youranimelist.ui.bottomnavigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kis.youranimelist.R


@Composable
fun MainScreenBottomNavigation(
    navController: NavController,
    navigationList: List<BottomNavigationDestinaton>,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    if (navigationList.map { it.screen_route }.contains(navBackStackEntry?.destination?.route)) {
        BottomNavigation(
            contentColor = Color.Black,
            modifier = Modifier
                .padding(end = 40.dp, start = 40.dp, bottom = 6.dp, top = 6.dp)
                .clip(RoundedCornerShape(30.dp))
        ) {
            val currentRoute = navBackStackEntry?.destination?.route
            navigationList.forEach { item ->
                BottomNavigationItem(
                    icon = {
                        Icon(painterResource(item.icon),
                            contentDescription = stringResource(id = R.string.default_content_description))
                    },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.White.copy(0.5f),
                    alwaysShowLabel = true,
                    selected = currentRoute == item.screen_route,
                    onClick = {
                        navController.popBackStack()
                        navController.navigate(item.screen_route)
                    }
                )
            }
        }
    }
}
