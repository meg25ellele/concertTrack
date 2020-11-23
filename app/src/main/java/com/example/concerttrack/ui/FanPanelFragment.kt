package com.example.concerttrack.ui

import android.content.Intent
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.example.concerttrack.viewmodel.FanGoingEventsViewModel
import com.example.concerttrack.viewmodel.FanMainPageViewModel
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

import kotlinx.android.synthetic.main.fan_panel_fragment.*
import kotlinx.android.synthetic.main.fan_panel_fragment.noPastEventsInfo
import kotlinx.android.synthetic.main.fan_panel_fragment.pastEventsInfo
import kotlinx.android.synthetic.main.fan_panel_fragment.pastEventsRV
import kotlinx.android.synthetic.main.fan_panel_fragment.progressBar
import kotlinx.android.synthetic.main.guest_info_fragment.*
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.*

class FanPanelFragment: Fragment() {

    private val fanMainPageViewModel: FanMainPageViewModel by lazy { ViewModelProvider(this).get(
        FanMainPageViewModel::class.java) }

    private val fanGoingEventsViewModel: FanGoingEventsViewModel by lazy { ViewModelProvider(this).get(
        FanGoingEventsViewModel::class.java) }

    private var allControls: List<View> = listOf()

    lateinit var comingEventsAdapter: EventsAdapter
    lateinit var pastEventsAdapter: EventsAdapter

    val comingEventsList  = mutableListOf<Event>()
    val pastEventsList = mutableListOf<Event>()
    val artistsMap = mutableMapOf<String, Artist>()
    val imagesMap = mutableMapOf<String, Uri>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if(userType == Constants.FAN_TYPE_STR) {

            logOutBtn.setOnClickListener {
                fanMainPageViewModel.logOut()
            }

            allControls = listOf(logOutBtn)

            comingEventsInfo.visibility = View.GONE
            pastEventsInfo.visibility = View.GONE
            noPastEventsInfo.visibility = View.GONE
            noComingEventsInfo.visibility = View.GONE

            comingEventsList.clear()
            pastEventsList.clear()
            artistsMap.clear()
            imagesMap.clear()

            val user = fanMainPageViewModel.getCurrentUser()
            fanMainPageViewModel.getFanData(user.email!!)


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
                    fanGoingEventsViewModel.removeEventFromMine(fan!!.id,event)
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
                            fanGoingEventsViewModel.addEventToMine(fan!!.id,event)
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
                    fanGoingEventsViewModel.removeEventFromMine(fan!!.id,event)
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
                            fanGoingEventsViewModel.addEventToMine(fan!!.id,event)
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


            fanMainPageViewModel.isFanLoggedOut.observe(viewLifecycleOwner, Observer<Boolean>{ loggedOut ->
                if(loggedOut) {
                    startActivity(Intent(activity, MainPageActivity::class.java))
                    activity?.finish()
                }

            })

            fanMainPageViewModel.fanLiveData.observe(viewLifecycleOwner, Observer {
                when(it) {
                    is Resource.Loading -> {
                        showSpinnerAndDisableControls()
                    }
                    is Resource.Success -> {
                        welcomeTxt.text = """Cześć ${it.data.name}!"""
                        fan = it.data
                        fanGoingEventsViewModel.retrieveFanEvents(it.data.id)
                    }
                    is Resource.Failure -> {
                        hideSpinnerAndEnableControls()
                        Toast.makeText(activity,it.throwable.message,Toast.LENGTH_LONG).show()
                    }
                }
            })

            fanGoingEventsViewModel.fanEventsLiveData.observe(viewLifecycleOwner, Observer {
                when(it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        comingEventsList.clear()
                        pastEventsList.clear()

                        comingEventsList.addAll(0,it.data.first)
                        pastEventsList.addAll(0,it.data.second)


                        if(it.data.first.size == 0 && it.data.second.size ==0) {
                            comingEventsInfo.visibility = View.VISIBLE
                            pastEventsInfo.visibility = View.VISIBLE
                            noComingEventsInfo.visibility = View.VISIBLE
                            noPastEventsInfo.visibility = View.VISIBLE
                            hideSpinnerAndEnableControls()
                        } else {
                            fanGoingEventsViewModel.getArtistsMap()
                        }
                    }
                    is Resource.Failure -> {
                        hideSpinnerAndEnableControls()
                        Toast.makeText(activity,it.throwable.message,Toast.LENGTH_LONG).show()
                    }
                }
            })

            fanGoingEventsViewModel.artistsMap.observe(viewLifecycleOwner, Observer {
                when(it) {
                    is Resource.Loading -> {
                        Log.i("fan","3")
                    }
                    is Resource.Success -> {
                        artistsMap.putAll(it.data)
                        fanGoingEventsViewModel.getArtistPhotos()
                    }
                    is Resource.Failure -> {
                        hideSpinnerAndEnableControls()
                    }
                }
            })

            fanGoingEventsViewModel.imagesMap.observe(viewLifecycleOwner, Observer {
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
                        hideSpinnerAndEnableControls()
                    }
                }
            })


        }

        if(userType == Constants.GUEST_TYPE_STR) {
            goToMainPageBtn.setOnClickListener {
                activity?.finish()
            }
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
            findNavController().navigate(
                R.id.action_fanPanelFragment_to_eventFragment,
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
            findNavController().navigate(
                R.id.action_fanPanelFragment_to_eventFragment,
                bundle
            )
        }
    }

    private fun showSpinnerAndDisableControls() {
        progressBar.visibility = View.VISIBLE
        allControls.forEach { v -> v.isEnabled = false }
    }

    private  fun hideSpinnerAndEnableControls() {
        progressBar.visibility = View.GONE
        allControls.forEach { v -> v.isEnabled = true }
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
        var fan: Fan? = null
    }
}