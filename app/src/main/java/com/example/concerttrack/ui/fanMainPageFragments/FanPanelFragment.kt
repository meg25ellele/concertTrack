package com.example.concerttrack.ui.fanMainPageFragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.ui.MainPageActivity
import com.example.concerttrack.viewmodel.FanMainPageViewModel
import kotlinx.android.synthetic.main.fan_panel_fragment.*

class FanPanelFragment: Fragment(R.layout.fan_panel_fragment) {

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

        logOutBtn.setOnClickListener {
            fanMainPageViewModel.logOut()
        }

    }
}