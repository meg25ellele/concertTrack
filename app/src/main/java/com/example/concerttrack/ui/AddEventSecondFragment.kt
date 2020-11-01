package com.example.concerttrack.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.concerttrack.R
import kotlinx.android.synthetic.main.add_event_second_fragment.*

class AddEventSecondFragment:Fragment(R.layout.add_event_second_fragment) {

    lateinit var header: String
    lateinit var startTime:String
    lateinit var startDate: String
    lateinit var description: String
    lateinit var ticketsLink: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        header = arguments?.getString(getString(R.string.header)).toString()
        startTime = arguments?.getString(getString(R.string.startTime)).toString()
        startDate = arguments?.getString(getString(R.string.startDate)).toString()
        description = arguments?.getString(getString(R.string.description)).toString()
        ticketsLink = arguments?.getString(getString(R.string.ticketsLink)).toString()

        backBtn.setOnClickListener {
            view.findNavController().popBackStack()
        }
    }
}