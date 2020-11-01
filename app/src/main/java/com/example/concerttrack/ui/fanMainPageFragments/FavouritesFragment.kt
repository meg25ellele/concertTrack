package com.example.concerttrack.ui.fanMainPageFragments

import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.ui.MainPageActivity
import com.example.concerttrack.util.Constants
import com.example.concerttrack.viewmodel.FanMainPageViewModel
import com.example.concerttrack.viewmodel.FanRegisterViewModel
import kotlinx.android.synthetic.main.guest_info_fragment.*

class FavouritesFragment: Fragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(FanPanelFragment.userType == Constants.GUEST_TYPE_STR) {
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
            inflater.inflate(R.layout.favourites_fragment,container,false)
        }
    }

    companion object {
        var userType = Constants.FAN_TYPE_STR
    }
}