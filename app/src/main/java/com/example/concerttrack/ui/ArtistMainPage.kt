package com.example.concerttrack.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.concerttrack.R
import com.example.concerttrack.viewmodel.ArtistMainPageViewModel
import com.example.concerttrack.viewmodel.FanMainPageViewModel
import kotlinx.android.synthetic.main.activity_artist_main_page.*


class ArtistMainPage : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_main_page)

        bottomNavigationArtistView.setupWithNavController(artistMainNavHostFragment.findNavController())

    }
}