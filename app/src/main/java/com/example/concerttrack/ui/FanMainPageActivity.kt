package com.example.concerttrack.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.concerttrack.R
import androidx.navigation.ui.setupWithNavController
import com.example.concerttrack.util.Constants
import com.example.concerttrack.viewmodel.FanMainPageViewModel
import kotlinx.android.synthetic.main.activity_fan_main_page.*

class FanMainPageActivity : AppCompatActivity() {

    private val fanMainPageViewModel: FanMainPageViewModel by lazy {
        ViewModelProvider(this).get(FanMainPageViewModel::class.java) }

    var userType = Constants.FAN_TYPE_STR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fan_main_page)

        userType = if (intent.getStringExtra(Constants.USER_TYPE) ==Constants.GUEST_TYPE_STR) Constants.GUEST_TYPE_STR else Constants.FAN_TYPE_STR


        FavouritesFragment.userType = userType
        FanPanelFragment.userType = userType



        bottomNavigationFanView.setupWithNavController(fanMainNavHostFragment.findNavController())

    }
}