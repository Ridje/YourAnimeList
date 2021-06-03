package com.kis.youranimelist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.kis.youranimelist.repository.RepositoryNetwork
import com.kis.youranimelist.ui.explore.ExploreFragment
import com.kis.youranimelist.ui.login.LoginFragment
import com.kis.youranimelist.utils.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            navigateToDefaultFragment();
        }
    }

    fun navigateToDefaultFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, getDefaultFragment()).commit()
    }

    fun navivageTo(fragment:Fragment) {
       supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()

    }

    fun navigateBack() {
        supportFragmentManager.popBackStack();
    }

    fun getDefaultFragment() : Fragment {

        val accessToken = AppPreferences.getInstance(applicationContext).readString(AppPreferences.ACCESS_TOKEN_SETTING_KEY)
        return when {
            accessToken.isBlank() -> LoginFragment()
            else -> ExploreFragment.newInstance()
        }
    }
}