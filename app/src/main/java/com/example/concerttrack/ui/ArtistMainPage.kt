package com.example.concerttrack.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.concerttrack.R
import com.example.concerttrack.models.Artist
import com.example.concerttrack.util.Resource
import com.example.concerttrack.viewmodel.ArtistMainPageViewModel
import kotlinx.android.synthetic.main.activity_artist_main_page.*
import kotlinx.android.synthetic.main.activity_artist_main_page.progressBar



class ArtistMainPage : AppCompatActivity() {

    private val artistMainPageViewModel: ArtistMainPageViewModel by lazy {
        ViewModelProvider(this).get(ArtistMainPageViewModel::class.java) }

    var artist: Artist? = null

    private var allControls: List<View> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_main_page)

        allControls = listOf(bottomNavigationArtistView)


        val user = artistMainPageViewModel.getCurrentUser()
        artistMainPageViewModel.getArtistData(user.email!!)

        bottomNavigationArtistView.setupWithNavController(artistMainNavHostFragment.findNavController())


        artistMainPageViewModel.artistLiveData.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    artist = it.data
                    ArtistPanelFragment.artist = artist
                    ArtistSettingsActivity.artist = artist
                    artistMainPageViewModel.getPhotoFromStorage(ArtistPanelFragment.artist!!.id)
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(this,it.throwable.toString(),Toast.LENGTH_LONG).show()
                }
            }
        })

        artistMainPageViewModel.photoUriLiveData.observe(this, Observer {
            when(it) {
                is Resource.Loading  ->{
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    Log.i("img","img")
                    ArtistPanelFragment.image = it.data
                    ArtistSettingsActivity.image = it.data
                    hideSpinnerAndEnableControls()
                }
                is Resource.Failure -> {
                    ArtistPanelFragment.image = null
                    ArtistSettingsActivity.image = null
                    hideSpinnerAndEnableControls()
                }
            }
        })

    }

    override fun onRestart() {
        super.onRestart()
        val user = artistMainPageViewModel.getCurrentUser()
        artistMainPageViewModel.getArtistData(user.email!!)
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