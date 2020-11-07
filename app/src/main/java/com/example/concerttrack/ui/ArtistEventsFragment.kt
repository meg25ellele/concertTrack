package com.example.concerttrack.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.concerttrack.R
import com.example.concerttrack.adapters.ArtistEventsAdapter
import com.example.concerttrack.adapters.GenresAdapter
import com.example.concerttrack.models.Event
import com.example.concerttrack.models.MusicGenre
import com.example.concerttrack.util.Resource
import com.example.concerttrack.viewmodel.ArtistEventsViewModel
import com.example.concerttrack.viewmodel.ArtistRegisterViewModel
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.artist_events_fragment.*

class ArtistEventsFragment: Fragment(R.layout.artist_events_fragment) {

    private val artistEventsVieModel: ArtistEventsViewModel by lazy { ViewModelProvider(this).get(
        ArtistEventsViewModel::class.java) }

    val comingEventsList  = mutableListOf<Event>()
    val pastEventsList = mutableListOf<Event>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        comingEventsList.clear()
        pastEventsList.clear()

        ArtistEventsViewModel.artistEventsLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    comingEventsList.clear()
                    pastEventsList.clear()
                    comingEventsList.addAll(0,it.data.first)
                    pastEventsList.addAll(0,it.data.second)
                    if(comingEventsList.size>0) {
                        noCommingEventsInfo.visibility = View.GONE
                    }
                    else {
                        noCommingEventsInfo.visibility = View.VISIBLE
                    }
                    if(pastEventsList.size>0) {
                        noPastEventsInfo.visibility = View.GONE
                    }
                    else {
                        noPastEventsInfo.visibility = View.VISIBLE
                    }
                    setAdapterAndManager()
                }
                is Resource.Failure-> {
                }
            }
        })
    }

    private fun setAdapterAndManager(){
        comingEventsRV.adapter = ArtistEventsAdapter(comingEventsList)
        comingEventsRV.layoutManager =LinearLayoutManager(activity)

        pastEventsRV.adapter =ArtistEventsAdapter(pastEventsList)
        pastEventsRV.layoutManager =LinearLayoutManager(activity)

    }

}