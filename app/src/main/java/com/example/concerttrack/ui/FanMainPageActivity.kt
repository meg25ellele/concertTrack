package com.example.concerttrack.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.example.concerttrack.R
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_fan_main_page.*

class FanMainPageActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fan_main_page)


        bottomNavigationFanView.setupWithNavController(fanMainNavHostFragment.findNavController())

    }
}