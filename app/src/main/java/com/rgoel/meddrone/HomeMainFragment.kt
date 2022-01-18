package com.rgoel.meddrone;

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment

class HomeMainFragment : Fragment() {

    private var PERMISSIONS_FINE_LOCATION = 99
    private lateinit var buttonhelpme: Button
    private lateinit var snake: ImageView
    private lateinit var accident: ImageView
    private lateinit var disaster: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_home_main, container, false)

        snake = view.findViewById(R.id.snake)
        accident = view.findViewById(R.id.accident)
        disaster = view.findViewById(R.id.disaster)
        snake.setOnClickListener {
            helpmetransition()
        }
        disaster.setOnClickListener {
            helpmetransition()
        }
        accident.setOnClickListener {
            helpmetransition()
        }
        return view
    }

    private fun helpmetransition() {
        activity?.let {
            val intent = Intent(it, HelpMeActivity::class.java)
            it.startActivity(intent)
        }
    }
}