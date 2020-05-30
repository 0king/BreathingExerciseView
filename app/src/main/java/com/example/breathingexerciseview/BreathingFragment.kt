package com.example.breathingexerciseview

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment

class BreathingFragment : Fragment() {

    private var timerMins = 2
    var isPlaying = false
    var currentTimeMillis = -1L

    //views
    lateinit var tvTimer : TextView
    lateinit var btnPlayPause : ImageButton
    lateinit var btnEdit : Button
    lateinit var btnRestart : Button
    lateinit var breathingView: BreathingCircleView
    lateinit var timer : CountDownTimer /*= object : CountDownTimer(timerMins * 60 * 1000L, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            //tvTimer.text = (millisUntilFinished/1000).toString()
        }
        override fun onFinish() {
            //tvTimer.text = "Completed"
        }
    }*/

    companion object {
        @JvmStatic
        fun newInstance(timeMinutes:Int) : BreathingFragment{
            val f = BreathingFragment()
            f.arguments = Bundle().apply {putInt(ARG_TIME, timeMinutes)}
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timerMins = arguments?.getInt(ARG_TIME)?:2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_breathing, container, false)

        tvTimer = rootView.findViewById(R.id.tvTimer)
        btnPlayPause = rootView.findViewById(R.id.btnToggle)
        btnEdit = rootView.findViewById(R.id.btnEdit)
        btnRestart = rootView.findViewById(R.id.btnRestart)
        breathingView = rootView.findViewById(R.id.breathingView)

        populateValues()

        btnPlayPause.setOnClickListener{
            if (isPlaying) pause() else resume()
        }
        btnEdit.setOnClickListener {showEditFragment()}
        btnRestart.setOnClickListener {restart()}
        return rootView
    }

    private fun showEditFragment(){
        fragmentManager?.beginTransaction()
            ?.replace(R.id.container_activity, EditFragment())
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun formatText(seconds : Long) : String{
        val mins = seconds / 60
        val secs = seconds % 60
        return "$mins:$secs"
    }

    private fun start(){
        //btnPlayPause.text = "Pause"
        breathingView.start()
        onHitStart()
    }

    private fun resume(){
        breathingView.resume()
        onHitStart()
    }

    private fun onHitStart(){
        isPlaying = true
        if (currentTimeMillis == -1L)
            startTimer(timerMins *60*1000L)
        else
            startTimer(currentTimeMillis)
    }

    private fun pause(){
        isPlaying = false
        pauseTimer()
        breathingView.pause()
        //btnPlayPause.text = "Play"
    }

    fun stop(){
        isPlaying = false
        pauseTimer()
        currentTimeMillis = -1
        breathingView.stop()
    }

    private fun restart(){
        isPlaying = true
        breathingView.start()
        startTimer(timerMins *60*1000L)
    }

    private fun startTimer(timerStartFrom: Long) {
        timer = object : CountDownTimer(timerStartFrom, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = formatText(millisUntilFinished/1000)
                currentTimeMillis = millisUntilFinished
            }
            override fun onFinish() {
                tvTimer.text = "Completed"
                breathingView.stop()
            }
        }.start()
    }

    fun pauseTimer() = timer?.cancel()

    private fun populateValues(){
        tvTimer.text = "$timerMins:00"

        val prefs = activity?.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val currentMode = prefs?.getString(KEY_MODE, "0")
        breathingView.inhaleSecs = prefs?.getString("${currentMode}_inhale", "4")?.toInt()?:4
        breathingView.inhalePauseSecs = prefs?.getString("${currentMode}_hold1", "3")?.toInt()?:3
        breathingView.exhaleSsecs = prefs?.getString("${currentMode}_exhale", "4")?.toInt()?:4
        breathingView.exhalePauseSecs = prefs?.getString("${currentMode}_hold2", "2")?.toInt()?:2
    }
}