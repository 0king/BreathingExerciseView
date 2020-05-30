package com.example.breathingexerciseview

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

const val ARG_TIME = "TIME_MINUTES"

class BreathingActivity : AppCompatActivity() {

    private var timerMins = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breathing)
        //timerMins = intent.extras?.getInt(ARG_TIME)?:2
        timerMins = intent.getIntExtra(ARG_TIME, 2)
        if (savedInstanceState == null) attachFragment()
    }

    private fun attachFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_activity, BreathingFragment.newInstance(timerMins))
            .commit()
    }

}
