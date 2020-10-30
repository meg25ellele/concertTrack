package com.example.concerttrack.ui.artistMainPageFragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.ui.MainPageActivity
import com.example.concerttrack.viewmodel.ArtistMainPageViewModel
import kotlinx.android.synthetic.main.artist_panel_fragment.*

class ArtistPanelFragment: Fragment(R.layout.artist_panel_fragment) {

    private val artistMainPageViewModel: ArtistMainPageViewModel by lazy { ViewModelProvider(this).get(
        ArtistMainPageViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        artistMainPageViewModel.isUserLoggedOut.observe(viewLifecycleOwner, Observer<Boolean>{ loggedOut ->
            if(loggedOut) {
                startActivity(Intent(activity,MainPageActivity::class.java))
                activity?.finish()
            }

        })

        logOutBtn.setOnClickListener {
            artistMainPageViewModel.logOut()
        }
    }

}