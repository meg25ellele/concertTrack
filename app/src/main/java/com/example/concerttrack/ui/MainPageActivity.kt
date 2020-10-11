package com.example.concerttrack.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.concerttrack.R
import com.example.concerttrack.util.Constants
import kotlinx.android.synthetic.main.activity_main_page.*

class MainPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        fanBtn.setOnClickListener {
            goToLoginActivity("fan")
        }

        artistBtn.setOnClickListener {
            goToLoginActivity("artist")
        }
    }

    private fun goToLoginActivity(userType: String) {
        val intent = Intent(this,LoginActivity::class.java).apply {
            putExtra(Constants.USER_TYPE,userType)
        }
        startActivity(intent)
    }
}