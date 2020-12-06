package com.example.concerttrack.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.concerttrack.R
import com.example.concerttrack.adapters.ArtistsAdapter
import com.example.concerttrack.adapters.EventsAdapter
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event
import com.example.concerttrack.models.Fan
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.EspressoIdlingResource
import com.example.concerttrack.util.Resource
import com.example.concerttrack.viewmodel.FanMainPageViewModel
import com.example.concerttrack.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.search_fragment.comingEventsInfo
import kotlinx.android.synthetic.main.search_fragment.comingEventsRV
import kotlinx.android.synthetic.main.search_fragment.noComingEventsInfo
import kotlinx.android.synthetic.main.search_fragment.noPastEventsInfo
import kotlinx.android.synthetic.main.search_fragment.pastEventsInfo
import kotlinx.android.synthetic.main.search_fragment.pastEventsRV

class SearchFragment : Fragment(R.layout.search_fragment) {


    private val searchViewModel: SearchViewModel by lazy { ViewModelProvider(this).get(
        SearchViewModel::class.java) }

    private val fanMainPageViewModel: FanMainPageViewModel by lazy { ViewModelProvider(this).get(
        FanMainPageViewModel::class.java) }

    private var allControls: List<View> = listOf()

    lateinit var comingEventsAdapter: EventsAdapter
    lateinit var pastEventsAdapter: EventsAdapter
    lateinit var artistsAdapter: ArtistsAdapter

    private val comingEventsList  = mutableListOf<Event>()
    private val pastEventsList = mutableListOf<Event>()
    private val artistList = mutableListOf<Artist>()

    private val recommendedComingEventsList  = mutableListOf<Event>()
    private val recommendedArtistsList = mutableListOf<Artist>()

    val artistsMap = mutableMapOf<String, Artist>()
    val imagesMap = mutableMapOf<String, Uri>()


    var word: String = ""
    var hasFavouritesArtists = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("created","created")

        allControls = listOf(searchEventArtistET)

        artistsInfo.visibility = View.GONE
        noArtistsInfo.visibility = View.GONE
        comingEventsInfo.visibility = View.GONE
        pastEventsInfo.visibility = View.GONE
        noPastEventsInfo.visibility = View.GONE
        noComingEventsInfo.visibility = View.GONE
        noRecommendedInfo.visibility = View.GONE

        artistList.clear()
        comingEventsList.clear()
        pastEventsList.clear()
        artistsMap.clear()
        imagesMap.clear()


        if(FanMainPageActivity.userType!=Constants.FAN_TYPE_STR) {
            noRecommendedInfo.visibility = View.VISIBLE
        } else {
            val user = fanMainPageViewModel.getCurrentUser()
            fanMainPageViewModel.getFanData(user.email!!)
        }

        searchEventArtistET.setOnEditorActionListener { v, actionId, keyEvent ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                keyEvent.action == KeyEvent.ACTION_DOWN ||
                keyEvent.action == KeyEvent.KEYCODE_ENTER) {

                word = searchEventArtistET.text.toString().toLowerCase()
                EspressoIdlingResource.countingIdlingResource.increment()
                if(word.isNotEmpty()) {
                    noRecommendedInfo.visibility = View.GONE
                    artistList.clear()
                    comingEventsList.clear()
                    pastEventsList.clear()
                    artistsMap.clear()
                    imagesMap.clear()
                    searchViewModel.searchForArtists(word)
                }
                else {
                    artistList.clear()
                    comingEventsList.clear()
                    pastEventsList.clear()

                    artistsInfo.visibility = View.GONE
                    noArtistsInfo.visibility = View.GONE
                    comingEventsInfo.visibility = View.GONE
                    pastEventsInfo.visibility = View.GONE
                    noPastEventsInfo.visibility = View.GONE
                    noComingEventsInfo.visibility = View.GONE

                    if(FanMainPageActivity.userType!=Constants.FAN_TYPE_STR) {
                        noRecommendedInfo.visibility = View.VISIBLE
                    } else {
                        if(!hasFavouritesArtists) {
                            noRecommendedInfo.text = resources.getString(R.string.noRecommendedInfoForFan)
                            noRecommendedInfo.visibility = View.VISIBLE
                        }
                        else {
                            setComingEventsRecyclerView(recommendedComingEventsList)
                            setArtistRecyclerView(recommendedArtistsList)
                        }
                    }
                }

            }
            false
        }

        fanMainPageViewModel.fanLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    fan = it.data
                    searchViewModel.hasFanFavouritesArtists(fan!!.id)
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(activity,it.throwable.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        searchViewModel.hasFavouritesArtistsLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if(it.data){
                        hasFavouritesArtists = true
                        noRecommendedInfo.text = resources.getString(R.string.noRecommendedInfoForFan)
                        noRecommendedInfo.visibility = View.GONE

                        searchViewModel.getFavouriteAndRecommendedArtists(fan!!.id)
                    } else {
                        hasFavouritesArtists = false
                        noRecommendedInfo.text = resources.getString(R.string.noRecommendedInfoForFan)
                        noRecommendedInfo.visibility = View.VISIBLE
                        hideSpinnerAndEnableControls()
                    }
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(activity,it.throwable.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        searchViewModel.favouritesAndRecommendedArtistsMap.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {

                    recommendedArtistsList.clear()
                    for(artist in (it.data["recommendedArtistsMap"] as Map<String,Artist>).values){
                        recommendedArtistsList.add(artist)
                    }

                    searchViewModel.getRecommendedComingEvents(it.data["favArtistsMap"] as Map<String,Artist>,
                        it.data["recommendedArtistsMap"] as Map<String,Artist>)
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(activity,it.throwable.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        searchViewModel.recommendedComingEventsResultsMap.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {

                    recommendedComingEventsList.clear()

                    recommendedComingEventsList.addAll(it.data)
                    searchViewModel.getArtistsMap()
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(activity,it.throwable.message, Toast.LENGTH_LONG).show()
                }
            }
        })


        searchViewModel.artistsResultMap.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    artistList.clear()

                    val artistsIDList = mutableListOf<String>()

                    for (artist in it.data.values) {
                        artistList.add(artist as Artist)
                        artistsIDList.add(artist.id)
                    }

                    searchViewModel.searchForEvents(word,artistsIDList)
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(activity,it.throwable.message,Toast.LENGTH_LONG).show()
                }
            }
        })

        searchViewModel.eventsResultsMap.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {

                    comingEventsList.clear()
                    pastEventsList.clear()

                    comingEventsList.addAll(0, it.data["comingEventsList"] as Collection<Event>)
                    pastEventsList.addAll(0, it.data["pastEventsList"] as Collection<Event>)

                    searchViewModel.getArtistsMap()

                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(activity,it.throwable.message,Toast.LENGTH_LONG).show()
                }
            }
        })


        searchViewModel.artistsMap.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    artistsMap.clear()
                    artistsMap.putAll(it.data)

                    searchViewModel.getArtistPhotos()

                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(activity,it.throwable.message,Toast.LENGTH_LONG).show()
                }
            }
        })

        searchViewModel.imagesMap.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    imagesMap.clear()

                   imagesMap.putAll(it.data)

                    if(word.isNotEmpty()) {
                        setComingEventsRecyclerView(comingEventsList)
                        setPastEventsRecyclerView(pastEventsList)
                        setArtistRecyclerView(artistList)
                        EspressoIdlingResource.countingIdlingResource.decrement()
                    } else {
                        setComingEventsRecyclerView(recommendedComingEventsList)
                        setArtistRecyclerView(recommendedArtistsList)
                    }



                    hideSpinnerAndEnableControls()
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(activity,it.throwable.message,Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun setComingEventsRecyclerView(comingEventsList: List<Event>) {
        comingEventsAdapter = EventsAdapter(comingEventsList,artistsMap,imagesMap)
        comingEventsRV.adapter = comingEventsAdapter
        comingEventsRV.layoutManager = LinearLayoutManager(activity)

        comingEventsInfo.visibility = View.VISIBLE
        if(word.isEmpty()){
            comingEventsInfo.text = getString(R.string.recommendedComingEventsInfo)
        } else {
            comingEventsInfo.text = getString(R.string.commingEventsInfo)
        }
        if(comingEventsList.isNotEmpty()) {
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
                putBoolean("isFan",FanMainPageActivity.userType== Constants.FAN_TYPE_STR)
                putBoolean("isPastEvent",false)
            }
            findNavController().navigate(
                R.id.action_searchFragment_to_eventFragment,
                bundle
            )
        }
    }

    private fun setPastEventsRecyclerView(pastEventsList: List<Event>) {
        pastEventsAdapter = EventsAdapter(pastEventsList,artistsMap,imagesMap)
        pastEventsRV.adapter = pastEventsAdapter
        pastEventsRV.layoutManager = LinearLayoutManager(activity)

        pastEventsInfo.visibility = View.VISIBLE
        if(pastEventsList.isNotEmpty()) {
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
                R.id.action_searchFragment_to_eventFragment,
                bundle
            )
        }
    }

    private fun setArtistRecyclerView(artistList: List<Artist>) {
        artistsAdapter = ArtistsAdapter(artistList,imagesMap)
        artistsInSearchRV.adapter = artistsAdapter
        artistsInSearchRV.layoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL)

        artistsInfo.visibility = View.VISIBLE
        if(word.isEmpty()) {
            artistsInfo.text = getString(R.string.recommendedArtistsInfo)
        } else {
            artistsInfo.text = getString(R.string.artistsInfo)
        }

        if(artistList.isNotEmpty()) {
            noArtistsInfo.visibility = View.GONE
        }
        else {
            noArtistsInfo.visibility = View.VISIBLE
        }

        artistsAdapter.setOnItemClickListener {artist: Artist, imgPath:String? ->
            val bundle = Bundle().apply {
                putSerializable("artist",artist)
                putString("imgPath",imgPath)
                putBoolean("isFan",FanMainPageActivity.userType==Constants.FAN_TYPE_STR)
            }
            parentFragment!!.findNavController().navigate(
                R.id.action_searchFragment_to_artistViewFragment,
                bundle
            )
        }
    }

    private fun showSpinnerAndDisableControls() {
        searchProgressBar.visibility = View.VISIBLE
        allControls.forEach { v -> v.isEnabled = false }

    }

    private  fun hideSpinnerAndEnableControls() {
        searchProgressBar.visibility = View.GONE
        allControls.forEach { v -> v.isEnabled = true }
    }

    companion object {
        var fan: Fan? = null
    }

}