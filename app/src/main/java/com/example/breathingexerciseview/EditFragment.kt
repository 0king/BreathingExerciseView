package com.example.breathingexerciseview

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

const val KEY_MODE = "CURRENT_MODE"

class EditFragment : Fragment() {

    enum class MODE{
        DEFAULT, PRANAYAMA, SQUARE, UJJAYI
    }

    lateinit var counterView1 : CounterView
    lateinit var counterView2 : CounterView
    lateinit var counterView3 : CounterView
    lateinit var counterView4 : CounterView
    lateinit var ibDefault : ImageButton
    lateinit var ibPranayama : ImageButton
    lateinit var ibSquare : ImageButton
    lateinit var ibUjjayi : ImageButton
    private lateinit var previousSelected: ImageButton
    lateinit var breathingView: BreathingCircleView

    var currentMode = MODE.DEFAULT
    //var KEY_INHALE = "${currentMode}_INHALE"//not working
    var KEY_INHALE = "_INHALE"
    var KEY_HOLD_1 = "_HOLD1"
    var KEY_EXHALE = "_EXHALE"
    var KEY_HOLD_2 = "_HOLD2"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_edit, container, false)
        counterView1 = rootView.findViewById(R.id.inhaleCounter)
        counterView2 = rootView.findViewById(R.id.hold1Counter)
        counterView3 = rootView.findViewById(R.id.exhaleCounter)
        counterView4 = rootView.findViewById(R.id.hold2Counter)
        ibDefault = rootView.findViewById(R.id.ibDefault)
        ibPranayama = rootView.findViewById(R.id.ibPranayama)
        ibSquare = rootView.findViewById(R.id.ibSquare)
        ibUjjayi = rootView.findViewById(R.id.ibUjjayi)
        breathingView = rootView.findViewById(R.id.breathingView)
        attachClickListeners()

        ibDefault.isSelected = true
        currentMode = MODE.DEFAULT
        populateValues()
        previousSelected = ibDefault
        return rootView
    }

    private fun attachClickListeners(){
        ibDefault.setOnClickListener {
            currentMode = MODE.DEFAULT
            onBtnClick(it)
        }
        ibPranayama.setOnClickListener {
            currentMode = MODE.PRANAYAMA
            onBtnClick(it)
        }
        ibSquare.setOnClickListener {
            currentMode = MODE.SQUARE
            onBtnClick(it)
        }
        ibUjjayi.setOnClickListener {
            currentMode = MODE.UJJAYI
            onBtnClick(it)
        }
        counterView1.setCounterListener(object : CounterView.CounterListener{
            override fun onDecClick(value: String?) {
                onValueChange(1, value)
            }
            override fun onIncClick(value: String?) {
                onValueChange(1, value)
            }
        })
        counterView2.setCounterListener(object : CounterView.CounterListener{
            override fun onDecClick(value: String?) {
                onValueChange(2, value)
            }
            override fun onIncClick(value: String?) {
                onValueChange(2, value)
            }
        })
        counterView3.setCounterListener(object : CounterView.CounterListener{
            override fun onDecClick(value: String?) {
                onValueChange(3, value)
            }
            override fun onIncClick(value: String?) {
                onValueChange(3, value)
            }
        })
        counterView4.setCounterListener(object : CounterView.CounterListener{
            override fun onDecClick(value: String?) {
                onValueChange(4, value)
            }
            override fun onIncClick(value: String?) {
                onValueChange(4, value)
            }
        })
    }

    private fun onBtnClick(it:View){
        it.isSelected = true
        previousSelected.isSelected = false
        previousSelected = it as ImageButton
        populateValues()
        save(KEY_MODE, currentMode.ordinal.toString())
    }

    private fun onValueChange(key:Int, newValue: String?){
        val v = newValue?.toInt()?:1
        when(key){
            1 -> {
                breathingView.inhaleSecs = v
                save("${currentMode}_inhale", newValue)
            }
            2 -> {
                breathingView.inhalePauseSecs = v
                save("${currentMode}_hold1", newValue)
            }
            3 -> {
                breathingView.exhaleSsecs = v
                save("${currentMode}_exhale", newValue)
            }
            4 -> {
                breathingView.exhalePauseSecs = v
                save("${currentMode}_hold2", newValue)
            }
        }
        breathingView.invalidate()
    }

    private fun populateValues(){
        val inhale = getValue("${currentMode}_inhale")
        counterView1.setStartCounterValue(inhale)
        breathingView.inhaleSecs = inhale.toInt()
        val hold1 = getValue("${currentMode}_hold1")
        counterView2.setStartCounterValue(hold1)
        breathingView.inhalePauseSecs = hold1.toInt()
        val exhale = getValue("${currentMode}_exhale")
        counterView3.setStartCounterValue(exhale)
        breathingView.exhaleSsecs = exhale.toInt()
        val hold2 = getValue("${currentMode}_hold2")
        counterView4.setStartCounterValue(hold2)
        breathingView.exhalePauseSecs = hold2.toInt()
        breathingView.invalidate()
    }

    private fun save(key:String, value:String?){
        val editor = activity?.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)?.edit()
        editor?.putString(key, value?:"1")?.apply()
    }

    private fun getValue(key: String):String{
        return activity
            ?.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            ?.getString(key, "3")?:"3"
    }

}
