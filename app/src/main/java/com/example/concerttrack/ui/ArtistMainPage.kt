package com.example.concerttrack.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.viewmodel.ArtistMainPageViewModel
import com.example.concerttrack.viewmodel.FanMainPageViewModel
import kotlinx.android.synthetic.main.activity_fan_main_page.*

class ArtistMainPage : AppCompatActivity() {

    private val artistMainPageViewModel: ArtistMainPageViewModel by lazy { ViewModelProvider(this).get(
        ArtistMainPageViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_main_page)

        val user =  artistMainPageViewModel.getCurrentUser()
        currentUser.text = user.email
        userName.text = user.displayName


        artistMainPageViewModel.isUserLoggedOut.observe(this, Observer<Boolean>{ loggedOut ->
            if(loggedOut) {
                startActivity(Intent(this,MainPageActivity::class.java))
                finish()
            }

        })

        logOut.setOnClickListener {
            artistMainPageViewModel.logOut()
        }
    }
}