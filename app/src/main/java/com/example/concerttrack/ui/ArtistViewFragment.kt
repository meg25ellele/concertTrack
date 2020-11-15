package com.example.concerttrack.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.concerttrack.R
import com.example.concerttrack.adapters.ArtistEventsAdapter
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.Resource
import com.example.concerttrack.viewmodel.ArtistEventsViewModel
import com.example.concerttrack.viewmodel.FanFavouritesArtistsViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.artist_view_fragment.*

class ArtistViewFragment: Fragment(R.layout.artist_view_fragment) {

    private val artistEventsViewModel: ArtistEventsViewModel by lazy { ViewModelProvider(this).get(
        ArtistEventsViewModel::class.java) }

    private val fanFavouritesArtistsViewModel: FanFavouritesArtistsViewModel by lazy { ViewModelProvider(this).get(
        FanFavouritesArtistsViewModel::class.java) }

    lateinit var artist: Artist

    var favouriteArtist = false

    lateinit var comingEventsAdapter: ArtistEventsAdapter
    lateinit var pastEventsAdapter: ArtistEventsAdapter

    val comingEventsList  = mutableListOf<Event>()
    val pastEventsList = mutableListOf<Event>()

    private var allControls: List<View> = listOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allControls = listOf(likeBtn)

        artist = arguments?.getSerializable("artist") as Artist
        loadData()
        setLinksButtons()

        comingEventsInfo.visibility = View.GONE
        pastEventsInfo.visibility = View.GONE
        noPastEventsInfo.visibility = View.GONE
        noComingEventsInfo.visibility = View.GONE

        comingEventsList.clear()
        pastEventsList.clear()

       artistEventsViewModel.retrieveArtistComingEvents(artist)
       artistEventsViewModel.retrieveArtistPastEvents(artist)

        artistEventsViewModel.artistComingEventsLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    comingEventsList.clear()
                    comingEventsList.addAll(0,it.data)
                    artistEventsViewModel.retrieveArtistPastEvents(artist)
                }
                is Resource.Failure-> {
                    hideSpinnerAndEnableControls()
                }
            }
        })

        artistEventsViewModel.artistPastEventsLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    pastEventsList.clear()
                    pastEventsList.addAll(0,it.data)

                    setComingEventsRecyclerView()
                    setPastEventsRecyclerView()
                    hideSpinnerAndEnableControls()
                }
                is Resource.Failure-> {
                    hideSpinnerAndEnableControls()

                }
            }
        })


    }

    private fun loadData() {

        nameTV.text = artist!!.name
        descTV.text = artist!!.description

        var genres = "?????"
        if(artist!!.myGenres !=null) {
            genres = ""
            for (genre in artist!!.myGenres!!) {
                genres+=genre
                genres+= "  "
            }
        }
        genresTV.text = genres

        val imgPath  = arguments?.getString("imgPath")
        if(imgPath !=null) {
            Picasso.get().load(imgPath.toUri()).into(avatarIV)
        }
        else {
            avatarIV.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.default_avatar)
        }

        if(!arguments?.getBoolean("isFan")!!) {
            likeBtn.visibility = View.GONE
        } else {
            favouriteArtist = FanMainPageActivity.fan!!.favouritesArtists.contains("artists/" + artist.id)

            if(favouriteArtist) {
                likeBtn.setImageDrawable(resources.getDrawable(R.drawable.heart_red))
            }

            likeBtn.setOnClickListener{
                if(favouriteArtist) {
                    likeBtn.setImageDrawable(resources.getDrawable(R.drawable.heart_empty))
                    fanFavouritesArtistsViewModel.removeArtistFromFavourites(FanMainPageActivity.fan!!.id,artist.id)
                    FanMainPageActivity.fan?.favouritesArtists?.remove("artists/" + artist.id)
                    favouriteArtist = false
                } else {
                    fanFavouritesArtistsViewModel.addArtistToFavourites(FanMainPageActivity.fan!!.id,artist.id)
                    FanMainPageActivity.fan?.favouritesArtists?.add("artists/" + artist.id)
                    likeBtn.setImageDrawable(resources.getDrawable(R.drawable.heart_red))
                    favouriteArtist = true
                }
            }
        }
    }

    private fun setLinksButtons() {

        if(artist!!.facebookLink!="") {
            fbBtn.setOnClickListener {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(artist!!.facebookLink))
                startActivity(browserIntent)
            }
        } else {
            fbBtn.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.fb_black)
        }


        if(artist!!.youtubeLink !="") {
            ytBtn.setOnClickListener {

                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(artist!!.youtubeLink))
                startActivity(browserIntent)
            }
        } else {
            ytBtn.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.youtube_black)
        }

        if(artist!!.spotifyLink!="") {
            spotiBtn.setOnClickListener {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(artist!!.spotifyLink))
                startActivity(browserIntent)
            }
        } else {
            spotiBtn.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.spotify_black)

        }
    }

    private fun setComingEventsRecyclerView() {
        comingEventsAdapter = ArtistEventsAdapter(comingEventsList,artist,arguments?.getString("imgPath"))
        comingEventsRV.adapter = comingEventsAdapter
        comingEventsRV.layoutManager = LinearLayoutManager(activity)

        comingEventsInfo.visibility = View.VISIBLE

        if(comingEventsList.size>0) {
            noComingEventsInfo.visibility = View.GONE
        }
        else {
            noComingEventsInfo.visibility = View.VISIBLE
        }
        comingEventsAdapter.setOnItemClickListener { event: Event, artist: Artist?, imgPath:String? ->
            val bundle = Bundle().apply {
                putSerializable("event",event)
                putSerializable("artist",artist)
                putString("imgPath",imgPath)
                putBoolean("isArtistEvent",false)
                putBoolean("isFan",FanMainPageActivity.userType==Constants.FAN_TYPE_STR)
                putBoolean("isPastEvent",false)
            }
            findNavController().navigate(
                R.id.action_artistViewFragment_to_eventFragment,
                bundle
            )
        }

    }

    private fun setPastEventsRecyclerView() {
        pastEventsAdapter =ArtistEventsAdapter(pastEventsList,artist,arguments?.getString("imgPath"))
        pastEventsRV.adapter =pastEventsAdapter
        pastEventsRV.layoutManager =LinearLayoutManager(activity)

        pastEventsInfo.visibility = View.VISIBLE
        if(pastEventsList.size>0) {
            noPastEventsInfo.visibility = View.GONE
        }
        else {
            noPastEventsInfo.visibility = View.VISIBLE
        }

        pastEventsAdapter.setOnItemClickListener { event: Event, artist: Artist?,imgPath: String? ->
            val bundle = Bundle().apply {
                putSerializable("event",event)
                putSerializable("artist",artist)
                putString("imgPath",imgPath)
                putBoolean("isArtistEvent",false)
                putBoolean("isFan",FanMainPageActivity.userType==Constants.FAN_TYPE_STR)
                putBoolean("isPastEvent",true)
            }
            findNavController().navigate(
                R.id.action_artistViewFragment_to_eventFragment,
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
}