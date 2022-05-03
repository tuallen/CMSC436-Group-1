package com.example.blockassessment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.blockassessment.ui.survey.Survey

class Hub : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hub_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, Survey.newInstance())
                .commitNow()
        }
    }
}