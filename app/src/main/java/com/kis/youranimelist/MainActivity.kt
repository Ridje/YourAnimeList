package com.kis.youranimelist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    lateinit var generalViewGroup:ConstraintLayout;
    lateinit var button:MaterialButton;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        generalViewGroup = findViewById(R.id.activity_main_layout);
        button = MaterialButton(this);
        button.text = "Start button";
        button.setOnClickListener({ button.text = "Processing..."})
        generalViewGroup.addView(button);
    }
}