package com.example.concerttrack.ui


import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.concerttrack.R
import androidx.navigation.ui.setupWithNavController
import com.example.concerttrack.adapters.EventsAdapter
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event
import com.example.concerttrack.models.Fan
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.Resource
import com.example.concerttrack.viewmodel.ArtistEventsViewModel
import com.example.concerttrack.viewmodel.ArtistMainPageViewModel
import com.example.concerttrack.viewmodel.EventsViewModel
import com.example.concerttrack.viewmodel.FanMainPageViewModel
import kotlinx.android.synthetic.main.activity_artist_settings.*
import kotlinx.android.synthetic.main.activity_fan_main_page.*
import kotlinx.android.synthetic.main.activity_fan_main_page.progressBar

class FanMainPageActivity : AppCompatActivity() {

    private val fanMainPageViewModel: FanMainPageViewModel by lazy {
        ViewModelProvider(this).get(FanMainPageViewModel::class.java) }

    private var allControls: List<View> = listOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fan_main_page)


        allControls = listOf(flFragment,bottomNavigationFanView)


        userType = if (intent.getStringExtra(Constants.USER_TYPE) ==Constants.GUEST_TYPE_STR) Constants.GUEST_TYPE_STR else Constants.FAN_TYPE_STR


        FavouritesFragment.userType = userType
        FanPanelFragment.userType = userType

        bottomNavigationFanView.setupWithNavController(fanMainNavHostFragment.findNavController())

        if(userType==Constants.FAN_TYPE_STR) {
            val user = fanMainPageViewModel.getCurrentUser()
            fanMainPageViewModel.getFanData(user.email!!)
        }

        fanMainPageViewModel.fanLiveData.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    fan = it.data
                    hideSpinnerAndEnableControls()
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(this,it.throwable.toString(), Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun showSpinnerAndDisableControls() {
        progressBar.visibility = View.VISIBLE
        allControls.forEach { v -> v.isEnabled = false }
    }

    private  fun hideSpinnerAndEnableControls() {
        progressBar.visibility = View.GONE
        allControls.forEach { v -> v.isEnabled = true }
    }

    companion object {
        var userType = Constants.FAN_TYPE_STR
        var fan: Fan? = null
    }


}