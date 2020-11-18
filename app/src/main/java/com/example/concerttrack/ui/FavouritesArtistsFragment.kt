package com.example.concerttrack.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.concerttrack.R
import com.example.concerttrack.adapters.ArtistsAdapter
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event
import com.example.concerttrack.models.Fan
import com.example.concerttrack.util.Resource
import com.example.concerttrack.viewmodel.FanFavouritesArtistsViewModel
import com.example.concerttrack.viewmodel.FanMainPageViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.favourites_artists_fragment.*


class FavouritesArtistsFragment: Fragment(R.layout.favourites_artists_fragment) {

    private val fanFavouritesArtistsViewModel: FanFavouritesArtistsViewModel by lazy { ViewModelProvider(this).get(
        FanFavouritesArtistsViewModel::class.java) }

    private val fanMainPageViewModel: FanMainPageViewModel by lazy { ViewModelProvider(this).get(
        FanMainPageViewModel::class.java) }

    private var allControls: List<View> = listOf()

    val favArtistList = mutableListOf<Artist>()
    val imagesMap = mutableMapOf<String, Uri>()

    lateinit var artistsAdapter: ArtistsAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouritesArtistsInfo.visibility = View.GONE
        noFavouritesArtistsInfo.visibility = View.GONE

        favArtistList.clear()
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
                    fanFavouritesArtistsViewModel.retrieveFavouritesArtists(it.data.id)
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(activity,it.throwable.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        fanFavouritesArtistsViewModel.fanFavouritesArtistsLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    favArtistList.clear()

                    favArtistList.addAll(0,it.data)

                    if(it.data.size== 0){
                        noFavouritesArtistsInfo.visibility = View.VISIBLE
                        favouritesArtistsInfo.visibility = View.VISIBLE
                        hideSpinnerAndEnableControls()
                    } else {
                        fanFavouritesArtistsViewModel.getArtistPhotos()
                    }
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(activity,it.throwable.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        fanFavouritesArtistsViewModel.imagesMap.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    imagesMap.putAll(it.data)

                    favouritesArtistsInfo.visibility = View.VISIBLE
                    setRecyclerView()
                    hideSpinnerAndEnableControls()
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                }
            }
        })

    }

    private fun setRecyclerView() {
        artistsAdapter = ArtistsAdapter(favArtistList,imagesMap)
        favouritesArtistsRV.adapter = artistsAdapter
        favouritesArtistsRV.layoutManager = GridLayoutManager(activity,2)
        //favouritesArtistsRV.addItemDecoration(Grid(2, d(10), true))
        favouritesArtistsRV.itemAnimator = DefaultItemAnimator()

        artistsAdapter.setOnItemClickListener {artist: Artist, imgPath:String? ->
            val bundle = Bundle().apply {
                putSerializable("artist",artist)
                putString("imgPath",imgPath)
                putBoolean("isFan",true)
            }
            parentFragment!!.findNavController().navigate(
                R.id.action_favouritesFragment2_to_artistViewFragment,
                bundle
            )
        }

        artistsAdapter.setOnHeartClickListener {artist: Artist, position:Int  ->
            favArtistList.removeAt(position)
            artistsAdapter.notifyItemRemoved(position)
            fanFavouritesArtistsViewModel.removeArtistFromFavourites(fan!!.id,artist.id)

            if(favArtistList.size >0) {
                noFavouritesArtistsInfo.visibility = View.GONE
            } else {
                noFavouritesArtistsInfo.visibility = View.VISIBLE
            }

            Snackbar.make(view!!,"Usunięto artystę",Snackbar.LENGTH_LONG).apply {
                setAction("Cofnij") {
                    favArtistList.add(position,artist)
                    artistsAdapter.notifyItemInserted(position)
                   fanFavouritesArtistsViewModel.addArtistToFavourites(fan!!.id,artist.id)

                    if(favArtistList.size >0) {
                        noFavouritesArtistsInfo.visibility = View.GONE
                    } else {
                        noFavouritesArtistsInfo.visibility = View.VISIBLE
                    }
                }
                 show()
            }
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