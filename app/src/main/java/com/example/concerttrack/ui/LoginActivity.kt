package com.example.concerttrack.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.concerttrack.R
import com.example.concerttrack.util.Constants
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val userTypeName = intent.getStringExtra(Constants.USER_TYPE)

        when(userTypeName) {
            Constants.ARTIST_TYPE_STR -> {
                loginInfo.text = getText(R.string.loginArtistInfo)
                userTypeIcon.setImageResource(R.drawable.star)
            }
            Constants.FAN_TYPE_STR -> {
                loginInfo.text = getText(R.string.loginFanInfo)
                userTypeIcon.setImageResource(R.drawable.user)

            }
        }
    }
}