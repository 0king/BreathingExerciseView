package com.example.breathingexerciseview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class EditFragment : Fragment() {

    lateinit var counterView1 : CounterView
    lateinit var counterView2 : CounterView
    lateinit var counterView3 : CounterView
    lateinit var counterView4 : CounterView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_edit, container, false)
        counterView1 = rootView.findViewById(R.id.inhaleCounter)
        counterView2 = rootView.findViewById(R.id.hold1Counter)
        counterView3 = rootView.findViewById(R.id.exhaleCounter)
        counterView4 = rootView.findViewById(R.id.hold2Counter)
        attachClickListeners()
        return rootView
    }

    private fun attachClickListeners(){
        counterView1.setCounterListener(object : CounterView.CounterListener{
            override fun onDecClick(value: String?) {

            }
            override fun onIncClick(value: String?) {

            }
        })

    }

}
