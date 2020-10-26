package com.example.concerttrack.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.viewmodel.FanMainPageViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_fan_main_page.*

class FanMainPageActivity : AppCompatActivity() {

    private val fanMainPageViewModel: FanMainPageViewModel by lazy { ViewModelProvider(this).get(
        FanMainPageViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fan_main_page)

       val user =  fanMainPageViewModel.getCurrentUser()
        currentUser.text = user.email
        userName.text = user.displayName


        fanMainPageViewModel.isUserLoggedOut.observe(this,Observer<Boolean>{ loggedOut ->
            if(loggedOut) {
                startActivity(Intent(this,MainPageActivity::class.java))
                finish()
            }

        })

        logOut.setOnClickListener {
            fanMainPageViewModel.logOut()
        }
    }
}