package com.example.breathingexerciseview

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class BreathingFragment : Fragment() {

    //val ARG_TIME = "TIME_MINS"
    private var timerMins = 2
    lateinit var tvTimer : TextView
    lateinit var btnPlayToggle : Button
    lateinit var btnEdit : Button
    lateinit var breathingView: BreathingCircleView
    var isPlaying = false
    var currentTimeMillis = -1L

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
            val bundle = Bundle()
            bundle.putInt(ARG_TIME, timeMinutes)
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timerMins = arguments?.getInt(ARG_TIME)?:2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_breathing, container, false)

        //timerMins = intent.extras?.getInt(ARG_TIME)?:2
        tvTimer = rootView.findViewById(R.id.tvTimer)
        btnPlayToggle = rootView.findViewById(R.id.btnToggle)
        btnEdit = rootView.findViewById(R.id.btnEdit)
        breathingView = rootView.findViewById(R.id.breathingView)

        tvTimer.text = "$timerMins mins"

        btnPlayToggle.setOnClickListener{
            if (isPlaying) pause() else start()
        }

        btnEdit.setOnClickListener {
            showEditFragment()
        }

        return rootView
    }

    private fun showEditFragment(){
        fragmentManager?.beginTransaction()
            ?.replace(R.id.container_activity, EditFragment())
            ?.addToBackStack(null)
            ?.commit()
    }

    fun start(){
        isPlaying = true
        //timer.start()
        btnPlayToggle.text = "Pause"
        breathingView.start()

        if (currentTimeMillis == -1L) startTimer(timerMins * 60* 1000L)
        else startTimer(currentTimeMillis)
    }

    fun pause(){
        isPlaying = false
        //timer.cancel()
        pauseTimer()

        breathingView.pause()
        btnPlayToggle.text = "Play"
    }

    private fun startTimer(timerStartFrom: Long) {
        timer = object : CountDownTimer(timerStartFrom, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = (millisUntilFinished/1000).toString()
                currentTimeMillis = millisUntilFinished
            }
            override fun onFinish() {
                tvTimer.text = "Completed"
            }
        }.start()
    }

    fun pauseTimer() = timer?.cancel()
}