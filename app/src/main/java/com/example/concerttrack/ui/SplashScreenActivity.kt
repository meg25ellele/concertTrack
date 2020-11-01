package com.example.concerttrack.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.util.Resource
import com.example.concerttrack.util.showToastError
import com.example.concerttrack.viewmodel.LoginViewModel
import com.example.concerttrack.viewmodel.SplashScreenViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class SplashScreenActivity : AppCompatActivity() {

    private val splashScreenViewModel: SplashScreenViewModel by lazy {
        ViewModelProvider(this).get(SplashScreenViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        splashScreenViewModel.checkUserLogin()

        splashScreenViewModel.userEmailLoginLiveData.observe(this, Observer<String?>{ loginUserEmail ->
            if(loginUserEmail!==null) {
                splashScreenViewModel.findArtist(loginUserEmail)
                splashScreenViewModel.findUser(loginUserEmail)
            } else {
                startActivity(Intent(this,MainPageActivity::class.java))
                finish()
            }
        })


        splashScreenViewModel.artistFound.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if(it.data) {
                        startActivity(Intent(this,ArtistMainPage::class.java))
                        finish()                    }
                }
                is Resource.Failure -> {
                    Toast.makeText(this,it.throwable.toString(), Toast.LENGTH_LONG).show()
                }
            }
        })

        splashScreenViewModel.userFound.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if(it.data) {
                        startActivity(Intent(this,FanMainPageActivity::class.java))
                        finish()
                    }
                }
                is Resource.Failure -> {
                    Toast.makeText(this,it.throwable.toString(), Toast.LENGTH_LONG).show()
                }
            }
        })

    }
}