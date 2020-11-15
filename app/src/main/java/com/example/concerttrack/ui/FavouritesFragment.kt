package com.example.concerttrack.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.concerttrack.R
import com.example.concerttrack.util.Constants
import kotlinx.android.synthetic.main.event_fragment.*
import kotlinx.android.synthetic.main.favourites_fragment.*
import kotlinx.android.synthetic.main.guest_info_fragment.*

class FavouritesFragment: Fragment() {

    var eventsBtnSelected = false
    var artistsBtnSelected = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(userType == Constants.GUEST_TYPE_STR) {
            goToMainPageBtn.setOnClickListener {
                activity?.finish()
            }
        }

        if(userType == Constants.FAN_TYPE_STR) {
            var selectedFragment: Fragment
            selectedFragment = InterestedEventsFragment()
            val ft : FragmentTransaction = childFragmentManager.beginTransaction()
            ft.replace(R.id.favouritesFL,selectedFragment)
            ft.commit()

            eventsBtnSelected = true
            favouritesArtistsBtn.setOnClickListener {
                if(!artistsBtnSelected) {
                    selectedFragment = FavouritesArtistsFragment()
                    val ft : FragmentTransaction = childFragmentManager.beginTransaction()
                    ft.replace(R.id.favouritesFL,selectedFragment)
                    ft.commit()
                    favouritesArtistsBtn.setBackgroundColor(resources.getColor(R.color.colorAccent))
                    interestedEventsBtn.setBackgroundColor(resources.getColor(R.color.buttonColor))
                    artistsBtnSelected = true
                    eventsBtnSelected = false
                }
            }
            interestedEventsBtn.setOnClickListener {
                if(!eventsBtnSelected) {
                    selectedFragment = InterestedEventsFragment()
                    val ft : FragmentTransaction = childFragmentManager.beginTransaction()
                    ft.replace(R.id.favouritesFL,selectedFragment)
                    ft.commit()
                    interestedEventsBtn.setBackgroundColor(resources.getColor(R.color.colorAccent))
                    favouritesArtistsBtn.setBackgroundColor(resources.getColor(R.color.buttonColor))
                    eventsBtnSelected = true
                    artistsBtnSelected = false
                }
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