package com.example.concerttrack.ui

import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.concerttrack.R
import com.example.concerttrack.adapters.EventsAdapter
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event
import com.example.concerttrack.models.Fan
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.Resource
import com.example.concerttrack.viewmodel.FanInterestedEventsViewModel
import com.example.concerttrack.viewmodel.FanMainPageViewModel
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fan_panel_fragment.*
import kotlinx.android.synthetic.main.favourites_fragment.*
import kotlinx.android.synthetic.main.interested_events_fragment.*
import kotlinx.android.synthetic.main.interested_events_fragment.comingEventsInfo
import kotlinx.android.synthetic.main.interested_events_fragment.comingEventsRV
import kotlinx.android.synthetic.main.interested_events_fragment.noComingEventsInfo
import kotlinx.android.synthetic.main.interested_events_fragment.noPastEventsInfo
import kotlinx.android.synthetic.main.interested_events_fragment.pastEventsInfo
import kotlinx.android.synthetic.main.interested_events_fragment.pastEventsRV
import kotlinx.android.synthetic.main.interested_events_fragment.progressBar
import java.time.ZonedDateTime


class InterestedEventsFragment: Fragment(R.layout.interested_events_fragment) {

    private val fanInterestedEventsViewModel: FanInterestedEventsViewModel by lazy { ViewModelProvider(this).get(
        FanInterestedEventsViewModel::class.java) }

    private val fanMainPageViewModel: FanMainPageViewModel by lazy { ViewModelProvider(this).get(
        FanMainPageViewModel::class.java) }

    private var allControls: List<View> = listOf()

    val comingEventsList  = mutableListOf<Event>()
    val pastEventsList = mutableListOf<Event>()

    val interestedEventsList  = mutableListOf<Event>()
    val artistsMap = mutableMapOf<String, Artist>()
    val imagesMap = mutableMapOf<String, Uri>()

    lateinit var comingEventsAdapter: EventsAdapter
    lateinit var pastEventsAdapter: EventsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allControls = listOf(favouritesArtistsBtn,interestedEventsBtn)

        noInterestedEventsInfo.visibility = View.GONE
        interestefEventsInfo.visibility = View.GONE
        comingEventsInfo.visibility = View.GONE
        pastEventsInfo.visibility = View.GONE
        noPastEventsInfo.visibility = View.GONE
        noComingEventsInfo.visibility = View.GONE


        interestedEventsList.clear()
        artistsMap.clear()
        imagesMap.clear()

        val user = fanMainPageViewModel.getCurrentUser()
        fanMainPageViewModel.getFanData(user.email!!)

        fanMainPageViewModel.fanLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    fan = it.data
                    fanInterestedEventsViewModel.retrieveInterestedEvents(it.data.id)
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(activity,it.throwable.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        fanInterestedEventsViewModel.fanInterestedEventsLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    comingEventsList.clear()
                    pastEventsList.clear()
                    for(event in it.data){
                        val parsedDate = ZonedDateTime.parse(event.startDateTime, Constants.DATE_TIME_FORMATTER)
                        if(parsedDate.isBefore(ZonedDateTime.now())) {
                            pastEventsList.add(event)
                        } else {
                            comingEventsList.add(event)
                        }
                    }
                    if(it.data.size == 0) {
                        noInterestedEventsInfo.visibility = View.VISIBLE
                        interestefEventsInfo.visibility = View.VISIBLE
                        hideSpinnerAndEnableControls()
                    } else {
                        fanInterestedEventsViewModel.getArtistsMap()
                    }
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(activity,it.throwable.message,Toast.LENGTH_LONG).show()
                }
            }
        })

        fanInterestedEventsViewModel.artistsMap.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    artistsMap.putAll(it.data)
                    fanInterestedEventsViewModel.getArtistPhotos()
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                }
            }
        })

        fanInterestedEventsViewModel.imagesMap.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    imagesMap.putAll(it.data)

                    interestefEventsInfo.visibility = View.VISIBLE
                    if(comingEventsList.size>0 && pastEventsList.size == 0) {
                        setComingEventsRecyclerView()
                    } else {
                        setComingEventsRecyclerView()
                        setPastEventsRecyclerView()
                    }

                    hideSpinnerAndEnableControls()
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                }
            }
        })

        val itemTouchHelperCallbackComing =object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position  = viewHolder.adapterPosition
                val event = comingEventsList[position]
                comingEventsList.removeAt(position)
                comingEventsAdapter.notifyItemRemoved(position)
                fanInterestedEventsViewModel.removeEventFromInterested(fan!!.id,event)
                if(comingEventsList.size>0) {
                    noComingEventsInfo.visibility = View.GONE
                }
                else {
                    noComingEventsInfo.visibility = View.VISIBLE
                }

                Snackbar.make(view,getString(R.string.eventDeleted), Snackbar.LENGTH_LONG).apply {
                    setAction(getString(R.string.undo)) {
                        comingEventsList.add(position,event)
                        comingEventsAdapter.notifyItemInserted(position)
                        fanInterestedEventsViewModel.addEventToInterested(fan!!.id,event)
                        if(comingEventsList.size>0) {
                            noComingEventsInfo.visibility = View.GONE
                        }
                        else {
                            noComingEventsInfo.visibility = View.VISIBLE
                        }

                    }
                    show()
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.delete_icon)
                    .create()
                    .decorate();

                super.onChildDraw(c, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive
                )
            }
        }

        ItemTouchHelper(itemTouchHelperCallbackComing).apply {
            attachToRecyclerView(comingEventsRV)
        }

        val itemTouchHelperCallbackPast =object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position  = viewHolder.adapterPosition
                val event = pastEventsList[position]
                pastEventsList.removeAt(position)
                pastEventsAdapter.notifyItemRemoved(position)
                fanInterestedEventsViewModel.removeEventFromInterested(fan!!.id,event)
                if(pastEventsList.size>0) {
                    noComingEventsInfo.visibility = View.GONE
                }
                else {
                    noComingEventsInfo.visibility = View.VISIBLE
                }

                Snackbar.make(view,getString(R.string.eventDeleted), Snackbar.LENGTH_LONG).apply {
                    setAction(getString(R.string.undo)) {
                        pastEventsList.add(position,event)
                        pastEventsAdapter.notifyItemInserted(position)
                        fanInterestedEventsViewModel.addEventToInterested(fan!!.id,event)
                        if(pastEventsList.size>0) {
                            noComingEventsInfo.visibility = View.GONE
                        }
                        else {
                            noComingEventsInfo.visibility = View.VISIBLE
                        }

                    }
                    show()
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(activity?.applicationContext!!,R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.delete_icon)
                    .create()
                    .decorate();

                super.onChildDraw(c, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive
                )
            }
        }

        ItemTouchHelper(itemTouchHelperCallbackPast).apply {
            attachToRecyclerView(pastEventsRV)
        }

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
                putBoolean("isFan",true)
                putBoolean("isPastEvent",false)
            }
            parentFragment!!.findNavController().navigate(
                R.id.action_favouritesFragment2_to_eventFragment,
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
                putBoolean("isFan",true)
                putBoolean("isPastEvent",true)
            }
            parentFragment!!.findNavController().navigate(
                R.id.action_favouritesFragment2_to_eventFragment,
                bundle
            )
        }
    }

    private fun showSpinnerAndDisableControls() {
        progressBar.visibility = View.VISIBLE
//        allControls.forEach { v -> v.isEnabled = false }
    }

    private  fun hideSpinnerAndEnableControls() {
        progressBar.visibility = View.GONE
        //allControls.forEach { v -> v.isEnabled = true }
    }

    companion object {
        var fan: Fan? = null
    }
}