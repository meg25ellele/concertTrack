package com.example.concerttrack.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.viewmodel.LoginViewModel
import com.example.concerttrack.viewmodel.SplashScreenViewModel
import com.google.firebase.auth.FirebaseUser

class SplashScreenActivity : AppCompatActivity() {

    private val splashScreenViewModel: SplashScreenViewModel by lazy {
        ViewModelProvider(this).get(SplashScreenViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        splashScreenViewModel.checkUserLogin()

        splashScreenViewModel.isUserLoginLiveData.observe(this, Observer<Boolean>{ isLoginUser ->
            if(isLoginUser) {

                startActivity(Intent(this,FanMainPageActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this,MainPageActivity::class.java))
                finish()
            }
        })

    }
}