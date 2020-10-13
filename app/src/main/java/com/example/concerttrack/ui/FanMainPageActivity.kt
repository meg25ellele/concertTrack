package com.example.concerttrack.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.viewmodel.LoggedInViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_fan_main_page.*

class FanMainPageActivity : AppCompatActivity() {

    private val loggedInViewModel: LoggedInViewModel by lazy { ViewModelProvider(this).get(
        LoggedInViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fan_main_page)


        loggedInViewModel.userLiveData?.observe(this,Observer<FirebaseUser>{ firebaseUser ->
            if(firebaseUser != null) {
                currentUser.text = firebaseUser.email
            }
        })

        loggedInViewModel.isLoginLiveData?.observe(this,Observer<Boolean>{ loggedIn ->
            if(!loggedIn) {
                finish()
            }

        })

        logOut.setOnClickListener {
            loggedInViewModel.logOut()
        }
    }
}