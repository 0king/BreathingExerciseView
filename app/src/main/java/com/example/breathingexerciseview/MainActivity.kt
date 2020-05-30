package com.example.breathingexerciseview

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker

const val SHARED_PREFS_NAME = "BREATHING_VALUES"
const val KEY_BREATHING_DURATION = "BREATHING_DURATION"

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
            //value = 2
            setFormatter { int:Int -> return@setFormatter "$int mins" }
            setOnValueChangedListener{ _, _, newVal -> updateValue(newVal) }
        }
        timerMins = getValue()
        numberPicker.value = timerMins
        findViewById<View>(R.id.btnStart).setOnClickListener{
            _ -> onBtnClick()
        }
    }

    private fun onBtnClick(){
        val i = Intent(this, BreathingActivity::class.java)
        i.putExtra(ARG_TIME, timerMins)
        startActivity(i)
    }

    fun updateValue(newVal:Int){
        timerMins = newVal
        saveValue(newVal)
    }

    fun saveValue(newVal: Int){
        val editor = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putInt(KEY_BREATHING_DURATION, newVal).apply()
    }

    fun getValue(): Int{
        val prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_BREATHING_DURATION, 2)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ARG_TIME, timerMins)
        super.onSaveInstanceState(outState)
    }
}
