package com.example.concerttrack.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.concerttrack.R
import com.example.concerttrack.adapters.ArtistEventsAdapter
import com.example.concerttrack.adapters.EventsAdapter
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.Resource
import com.example.concerttrack.viewmodel.EventsViewModel
import kotlinx.android.synthetic.main.activity_fan_main_page.*
import kotlinx.android.synthetic.main.events_fragment.*
import kotlinx.android.synthetic.main.events_fragment.noPastEventsInfo
import kotlinx.android.synthetic.main.events_fragment.progressBar

class EventsFragment: Fragment(R.layout.events_fragment) {

    private val eventsViewModel: EventsViewModel by lazy { ViewModelProvider(this).get(
        EventsViewModel::class.java) }

    private var mContext: Context? = null

    private var allControls: List<View> = listOf()

    lateinit var comingEventsAdapter: EventsAdapter
    lateinit var pastEventsAdapter: EventsAdapter

    val comingEventsList  = mutableListOf<Event>()
    val pastEventsList = mutableListOf<Event>()
    val artistsMap = mutableMapOf<String,Artist>()
    val imagesMap = mutableMapOf<String, Uri>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allControls = listOf(bottomNavigationFanView)


        eventsViewModel.retrieveComingEvents()

        comingEventsInfo.visibility = View.GONE
        pastEventsInfo.visibility = View.GONE
        noPastEventsInfo.visibility = View.GONE
        noComingEventsInfo.visibility = View.GONE

        comingEventsList.clear()
        pastEventsList.clear()
        artistsMap.clear()
        imagesMap.clear()

        eventsViewModel.comingEventsLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    comingEventsList.clear()
                    comingEventsList.addAll(0,it.data)
                    eventsViewModel.retrieveArtistPastEvents()
                }
                is Resource.Failure-> {
                    Log.i("events1",it.throwable.toString())
                    hideSpinnerAndEnableControls()
                }
            }
        })

        eventsViewModel.pastEventsLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    pastEventsList.clear()
                    pastEventsList.addAll(0,it.data)
                    eventsViewModel.getArtistsMap()
                }
                is Resource.Failure-> {
                    Log.i("events2",it.throwable.toString())
                    hideSpinnerAndEnableControls()

                }
            }
        })
        eventsViewModel.artistsMap.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    artistsMap.putAll(it.data)
                    eventsViewModel.getArtistPhotos()
                }
                is Resource.Failure -> {
                    Log.i("events3",it.throwable.toString())
                    hideSpinnerAndEnableControls()
                }
            }
        })

        eventsViewModel.imagesMap.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    imagesMap.putAll(it.data)
                    setComingEventsRecyclerView()
                    setPastEventsRecyclerView()
                    hideSpinnerAndEnableControls()
                }
                is Resource.Failure -> {
                    Log.i("events4",it.throwable.toString())
                    hideSpinnerAndEnableControls()
                }
            }
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun showSpinnerAndDisableControls() {
        progressBar.visibility = View.VISIBLE
//        allControls.forEach { v -> v.isEnabled = false }
    }

    private  fun hideSpinnerAndEnableControls() {
        progressBar.visibility = View.GONE
      //  allControls.forEach { v -> v.isEnabled = true }
    }


    private fun setComingEventsRecyclerView() {
        comingEventsAdapter = EventsAdapter(comingEventsList,artistsMap,imagesMap)
        comingEventsRV.adapter = comingEventsAdapter
        comingEventsRV.layoutManager = LinearLayoutManager(activity)

        comingEventsInfo.visibility = View.VISIBLE
        if(comingEventsList.size>0) {
            noComingEventsInfo.visibility = View.GONE
        }
        else {
            noComingEventsInfo.visibility = View.VISIBLE
        }

        comingEventsAdapter.setOnItemClickListener { event: Event, artist: Artist, imgPath:String? ->
            val bundle = Bundle().apply {
                putSerializable("event",event)
                putSerializable("artist",artist)
                putString("imgPath",imgPath)
                putBoolean("isArtistEvent",false)
                putBoolean("isFan",FanMainPageActivity.userType==Constants.FAN_TYPE_STR)
                putBoolean("isPastEvent",false)
            }
            findNavController().navigate(
                R.id.action_eventsFragment3_to_eventFragment,
                bundle
            )
        }
    }

    private fun setPastEventsRecyclerView() {
        pastEventsAdapter = EventsAdapter(pastEventsList,artistsMap,imagesMap)
        pastEventsRV.adapter = pastEventsAdapter
        pastEventsRV.layoutManager = LinearLayoutManager(activity)

        pastEventsInfo.visibility = View.VISIBLE
        if(pastEventsList.size>0) {
            noPastEventsInfo.visibility = View.GONE
        }
        else {
            noPastEventsInfo.visibility = View.VISIBLE
        }

        pastEventsAdapter.setOnItemClickListener { event: Event, artist: Artist,imgPath: String? ->
            val bundle = Bundle().apply {
                putSerializable("event",event)
                putSerializable("artist",artist)
                putString("imgPath",imgPath)
                putBoolean("isArtistEvent",false)
                putBoolean("isFan",FanMainPageActivity.userType==Constants.FAN_TYPE_STR)
                putBoolean("isPastEvent",true)
            }
            findNavController().navigate(
                R.id.action_eventsFragment3_to_eventFragment,
                bundle
            )
        }
    }

}





