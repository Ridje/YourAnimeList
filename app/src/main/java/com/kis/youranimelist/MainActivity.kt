package com.kis.youranimelist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    lateinit var generalViewGroup:LinearLayoutCompat;
    lateinit var button:MaterialButton;
    lateinit var buttonStatus:ViewStatus;
    lateinit var buttonCopy:MaterialButton;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        generalViewGroup = findViewById(R.id.activity_main_layout)
        buttonStatus = ViewStatus()
        button = MaterialButton(this)
        button.text = ViewStatus.statuses[buttonStatus.currentStatus]
        button.setOnClickListener {
            buttonStatus.currentStatus = if (buttonStatus.currentStatus == 1) 0 else 1
            button.text = ViewStatus.statuses[buttonStatus.currentStatus]
            for (i in 0..9) {
                System.out.println(i.toString())
            }
        }
        generalViewGroup.addView(button)
        buttonCopy = MaterialButton(this)
        buttonCopy.text = "Copy"
        generalViewGroup.addView(buttonCopy)
        var dataForCopy = ActivityContent("Arnold", 27)
        buttonCopy.setOnClickListener {
            val newData = dataForCopy.copy(age = dataForCopy.age + 1)
            dataForCopy = newData
            val newButton = MaterialButton(this)
            newButton.text = newData.name + " " + newData.age
            generalViewGroup.addView(newButton)
            System.out.println(String.format("object:#%s name:%s age:%d", newData.hashCode(), newData.name, newData.age))
        }
    }
}

data class ViewStatus(var currentStatus: Int = 0) {
    companion object {
        val statuses:List<String> = listOf("Start", "Stop")
    }
}

data class ActivityContent(val name:String, val age:Int)
