package com.kis.youranimelist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kis.youranimelist.ui.explore.ExploreFragment
import com.kis.youranimelist.ui.login.LoginFragment
import com.kis.youranimelist.utils.AppPreferences
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    lateinit var root : FrameLayout

    private val airmodeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (root != null)
            {
                val airplane = AppCompatImageView(root.context)
                airplane.background = ContextCompat.getDrawable(root.context, R.drawable.ic_airplane)
                root.addView(airplane)
                airplane.layoutParams.width = Math.round(airplane.resources.getDimension(R.dimen.air_size))
                airplane.layoutParams.height = Math.round(airplane.resources.getDimension(R.dimen.air_size))
                airplane.rotation = 120f
                airplane.animate().setDuration(5000).translationX(2000f).translationY(4000f).start()
                root.removeView(airplane)
            }
        }
    }

    override fun onDestroy() {
        applicationContext?.let {
            it.unregisterReceiver(airmodeReceiver)
        }
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            navigateToDefaultFragment();
        }
        root = findViewById(R.id.root_layout)
        applicationContext?.registerReceiver(airmodeReceiver, IntentFilter("android.intent.action.AIRPLANE_MODE"))
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