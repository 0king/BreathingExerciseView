package com.example.breathingexerciseview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker

class MainActivity : AppCompatActivity() {

    lateinit var numberPicker:NumberPicker
    private var timerMins = 2
    //private val listener = NumberPicker.OnValueChangeListener() //todo - check

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker = findViewById(R.id.numberPicker)
        numberPicker.apply{
            minValue = 1
            maxValue = 10000
            value = 2
            setFormatter { int:Int -> return@setFormatter "$int mins" }
            setOnValueChangedListener{ _, _, newVal -> timerMins = newVal }
        }
        findViewById<View>(R.id.btnStart).setOnClickListener{
            _ -> onBtnClick()
        }
    }

    private fun onBtnClick(){
        val i = Intent(this, BreathingActivity::class.java)
        i.putExtra("TIME_MINS", timerMins)
        startActivity(i)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("TIME", timerMins)
        super.onSaveInstanceState(outState)
    }
}

/*
*
        *
        *handler
        *timer task
        *

    override fun onDetachedFromWindow() {
        waveAnimator?.cancel()
        super.onDetachedFromWindow()
    }
    *
*
* */