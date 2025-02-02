package com.example.concerttrack.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.util.Constants
import com.example.concerttrack.viewmodel.FanRegisterViewModel
import com.example.concerttrack.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main_page.*

class MainPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        fanBtn.setOnClickListener {
            goToLoginActivity(Constants.FAN_TYPE_STR)
        }

        artistBtn.setOnClickListener {
            goToLoginActivity(Constants.ARTIST_TYPE_STR)
        }

        useAppWithoutLoginBtn.setOnClickListener {
            val intent = Intent(this,FanMainPageActivity::class.java).apply {
                putExtra(Constants.USER_TYPE,Constants.GUEST_TYPE_STR)
            }
            startActivity(intent)
        }
    }

    private fun goToLoginActivity(userType: String) {
        val intent = Intent(this,LoginActivity::class.java).apply {
            putExtra(Constants.USER_TYPE,userType)
        }
        startActivity(intent)
    }

}