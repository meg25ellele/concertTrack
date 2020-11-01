package com.example.concerttrack.ui.fanMainPageFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.ui.MainPageActivity
import com.example.concerttrack.util.Constants
import com.example.concerttrack.viewmodel.FanMainPageViewModel
import kotlinx.android.synthetic.main.fan_panel_fragment.*
import kotlinx.android.synthetic.main.guest_info_fragment.*

class FanPanelFragment: Fragment() {

    private val fanMainPageViewModel: FanMainPageViewModel by lazy { ViewModelProvider(this).get(
        FanMainPageViewModel::class.java) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fanMainPageViewModel.isUserLoggedOut.observe(viewLifecycleOwner, Observer<Boolean>{ loggedOut ->
            if(loggedOut) {
                startActivity(Intent(activity, MainPageActivity::class.java))
                activity?.finish()
            }

        })

        if(userType == Constants.FAN_TYPE_STR) {
            logOutBtn.setOnClickListener {
                fanMainPageViewModel.logOut()
            }
        }

        if(userType == Constants.GUEST_TYPE_STR) {
            goToMainPageBtn.setOnClickListener {
                activity?.finish()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return if(userType == Constants.GUEST_TYPE_STR) {
            inflater.inflate(R.layout.guest_info_fragment,container,false)
        } else {
            inflater.inflate(R.layout.fan_panel_fragment,container,false)
        }
    }

    companion object {
        var userType = Constants.FAN_TYPE_STR
    }
}