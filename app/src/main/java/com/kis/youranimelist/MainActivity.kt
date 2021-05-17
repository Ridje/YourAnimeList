package com.kis.youranimelist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kis.youranimelist.ui.explore.ExploreFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ExploreFragment.newInstance())
                .commitNow()
        }
    }
}