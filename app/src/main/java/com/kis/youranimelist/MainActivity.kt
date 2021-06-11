package com.kis.youranimelist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kis.youranimelist.databinding.MainActivityBinding
import com.kis.youranimelist.ui.explore.ExploreFragment
import com.kis.youranimelist.ui.history.HistoryFragment
import com.kis.youranimelist.ui.login.LoginFragment
import com.kis.youranimelist.ui.settings.SettingsFragment
import com.kis.youranimelist.utils.AppPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: MainActivityBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.bottomNavigationMenu.setOnNavigationItemSelectedListener { item -> switchNavigation(item.itemId) }
        if (savedInstanceState == null) {
            navigateToDefaultFragment();
        }
    }

    private fun switchNavigation(item: Int): Boolean {
        when (item) {
            R.id.navigation_home -> navigateToDefaultFragment()
            R.id.navigation_favourites -> navigateTo(HistoryFragment())
            R.id.navigation_settings -> navigateTo(SettingsFragment())
        }

        return true
    }

    fun setVisibilityBottomNavigationMenu(visibility: Int) {
        binding.bottomNavigationMenu.visibility = visibility
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setVisibilityBottomNavigationMenu(View.VISIBLE)
    }
}

fun FragmentActivity.navigateToDefaultFragment() {
    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, getDefaultFragment()).commit()
}

fun FragmentActivity.navigateTo(fragment:Fragment, addToBackStack: Boolean = false) {
    val trans = supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
    if (addToBackStack) {
        trans.addToBackStack(null)
    }
    trans.commit()
}


fun FragmentActivity.navigateBack() {
    supportFragmentManager.popBackStack();
}

fun FragmentActivity.getDefaultFragment() : Fragment {

    val accessToken = AppPreferences.getInstance(applicationContext).readString(AppPreferences.ACCESS_TOKEN_SETTING_KEY)
    return when {
        accessToken.isBlank() -> LoginFragment()
        else -> ExploreFragment.newInstance()
    }
}
