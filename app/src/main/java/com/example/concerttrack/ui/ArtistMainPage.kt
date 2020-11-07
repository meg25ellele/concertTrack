package com.example.concerttrack.ui


import android.net.Uri
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
import com.example.concerttrack.viewmodel.ArtistEventsViewModel
import com.example.concerttrack.viewmodel.ArtistMainPageViewModel
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_artist_main_page.*
import kotlinx.android.synthetic.main.activity_artist_main_page.progressBar



class ArtistMainPage : AppCompatActivity() {

    private val artistMainPageViewModel: ArtistMainPageViewModel by lazy {
        ViewModelProvider(this).get(ArtistMainPageViewModel::class.java) }

    private val artistEventsVieModel: ArtistEventsViewModel by lazy { ViewModelProvider(this).get(
        ArtistEventsViewModel::class.java) }

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
                    artistReference = it.data.first
                    artist =it.data.second
                    ArtistPanelFragment.artist = artist
                    ArtistSettingsActivity.artist = artist
                    artistMainPageViewModel.getPhotoFromStorage(ArtistPanelFragment.artist!!.id)
                    artistEventsVieModel.retrieveArtistEvents(it.data.first!!)
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
                    image = it.data
                    ArtistPanelFragment.image = image
                    ArtistSettingsActivity.image= image
                    hideSpinnerAndEnableControls()
                }
                is Resource.Failure -> {
                   image = null
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

    companion object {
        var artistReference: DocumentReference? = null
        var artist: Artist? = null
        var image: Uri? =  null
    }

}