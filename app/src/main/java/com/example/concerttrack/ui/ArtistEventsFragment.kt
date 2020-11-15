package com.example.concerttrack.ui

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.concerttrack.R
import com.example.concerttrack.adapters.ArtistEventsAdapter
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event
import com.example.concerttrack.util.Resource
import com.example.concerttrack.viewmodel.ArtistEventsViewModel
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.artist_events_fragment.*

class ArtistEventsFragment: Fragment(R.layout.artist_events_fragment) {

    private val artistEventsVieModel: ArtistEventsViewModel by lazy { ViewModelProvider(this).get(
        ArtistEventsViewModel::class.java) }

    lateinit var comingEventsAdapter: ArtistEventsAdapter
    lateinit var pastEventsAdapter: ArtistEventsAdapter

    val comingEventsList  = mutableListOf<Event>()
    val pastEventsList = mutableListOf<Event>()

    private var mContext: Context? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        comingEventsList.clear()
        pastEventsList.clear()

        noComingEventsInfo.visibility = View.GONE
        noPastEventsInfo.visibility = View.GONE
        noComingEventsInfo.visibility = View.GONE
        pastEventsInfo.visibility = View.GONE


        val itemTouchHelperCallback =object:ItemTouchHelper.SimpleCallback(0,
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
                artistEventsVieModel.deleteArtistEvent(event)
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
                        artistEventsVieModel.addArtistEvent(event)
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

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(comingEventsRV)
        }


        ArtistEventsViewModel.artistComingEventsLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    comingEventsList.clear()
                    comingEventsList.addAll(0,it.data)

                    setComingEventsRecyclerView()
                }
                is Resource.Failure-> {
                }
            }
        })

        ArtistEventsViewModel.artistPastEventsLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    pastEventsList.clear()
                    pastEventsList.addAll(0,it.data)
                    setPastEventsRecyclerView()
                }
                is Resource.Failure-> {

                }
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun setComingEventsRecyclerView() {
        comingEventsAdapter = ArtistEventsAdapter(comingEventsList,ArtistMainPageActivity.artist!!,null)
        comingEventsRV.adapter = comingEventsAdapter
        comingEventsRV.layoutManager =LinearLayoutManager(activity)

        comingEventsInfo.visibility = View.VISIBLE

        if(comingEventsList.size>0) {
            noComingEventsInfo.visibility = View.GONE
        }
        else {
            noComingEventsInfo.visibility = View.VISIBLE
        }

        comingEventsAdapter.setOnItemClickListener { event: Event, artist: Artist?, s: String? ->
            val bundle = Bundle().apply {
                putSerializable("event", event)
                putBoolean("isArtistEvent",true)
                putBoolean("isFan",false)
                putBoolean("isPastEvent",false)
            }
            findNavController().navigate(
                R.id.action_eventsFragment2_to_eventFragment3,
                bundle
            )
        }
    }

    private fun setPastEventsRecyclerView() {
        pastEventsAdapter =ArtistEventsAdapter(pastEventsList,ArtistMainPageActivity.artist!!,null)
        pastEventsRV.adapter =pastEventsAdapter
        pastEventsRV.layoutManager =LinearLayoutManager(activity)

        pastEventsInfo.visibility = View.VISIBLE
        if(pastEventsList.size>0) {
            noPastEventsInfo.visibility = View.GONE
        }
        else {
            noPastEventsInfo.visibility = View.VISIBLE
        }

        pastEventsAdapter.setOnItemClickListener { event: Event, artist: Artist?, s: String? ->
            val bundle = Bundle().apply {
                putSerializable("event", event)
                putBoolean("isArtistEvent",true)
                putBoolean("isFan",false)
                putBoolean("isPastEvent",true)
            }
            findNavController().navigate(
                R.id.action_eventsFragment2_to_eventFragment3,
                bundle
            )
        }
    }

}