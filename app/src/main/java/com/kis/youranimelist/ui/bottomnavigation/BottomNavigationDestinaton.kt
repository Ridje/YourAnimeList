package com.kis.youranimelist.ui.bottomnavigation

import com.kis.youranimelist.R
import com.kis.youranimelist.ui.navigation.NavigationKeys

sealed class BottomNavigationDestinaton(
    val title: String?,
    val icon: Int,
    var screen_route: String,
) {
    object Explore :
        BottomNavigationDestinaton(null, R.drawable.ic_explore, NavigationKeys.Route.EXPLORE)

    object Profile :
        BottomNavigationDestinaton(null, R.drawable.ic_user, NavigationKeys.Route.PROFILE)

    object MyList :
        BottomNavigationDestinaton(null, R.drawable.ic_list_ol, NavigationKeys.Route.MY_LIST)
}
