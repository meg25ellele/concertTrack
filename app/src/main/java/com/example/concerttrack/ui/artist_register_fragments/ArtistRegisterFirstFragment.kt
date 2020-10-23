package com.example.concerttrack.ui.artist_register_fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.concerttrack.R
import kotlinx.android.synthetic.main.fragment_artist_register_first.*

class ArtistRegisterFirstFragment: Fragment(R.layout.fragment_artist_register_first) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextFragmentBtn.setOnClickListener {view ->
            view.findNavController().navigate(R.id.action_artistRegisterFirstFragment_to_artistRegisterSecondFragment)
        }
    }
}