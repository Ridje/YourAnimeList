package com.kis.youranimelist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.kis.youranimelist.ui.explore.ExploreFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            navivageTo(ExploreFragment.newInstance(), false)
        }
    }

    fun navivageTo(fragment:Fragment, addToBackstack:Boolean) {
        val fragment = supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
        if (addToBackstack) {
            fragment.addToBackStack(null)
        }
        fragment.commit()

    }
}