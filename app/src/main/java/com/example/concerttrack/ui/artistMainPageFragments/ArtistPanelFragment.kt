package com.example.concerttrack.ui.artistMainPageFragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.concerttrack.R
import com.example.concerttrack.models.Artist
import com.example.concerttrack.ui.ArtistSettingsActivity
import com.example.concerttrack.ui.MainPageActivity
import com.example.concerttrack.util.Resource
import com.example.concerttrack.viewmodel.ArtistMainPageViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_artist_main_page.*
import kotlinx.android.synthetic.main.activity_artist_settings.*
import kotlinx.android.synthetic.main.artist_panel_fragment.*
import kotlinx.android.synthetic.main.artist_panel_fragment.avatarIV
import kotlinx.android.synthetic.main.artist_panel_fragment.progressBar

class ArtistPanelFragment: Fragment(R.layout.artist_panel_fragment) {

    private val artistMainPageViewModel: ArtistMainPageViewModel by lazy { ViewModelProvider(this).get(
        ArtistMainPageViewModel::class.java) }


    private var allControls: List<View> = listOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLinksButtons()
        loadData()

        allControls = listOf(logOutBtn,settingsBtn,fbBtn,ytBtn,spotiBtn)

        artistMainPageViewModel.isUserLoggedOut.observe(viewLifecycleOwner, Observer<Boolean>{ loggedOut ->
            if(loggedOut) {
                startActivity(Intent(activity,MainPageActivity::class.java))
                activity?.finish()
            }
        })

        logOutBtn.setOnClickListener {
            artistMainPageViewModel.logOut()
        }

        settingsBtn.setOnClickListener {
            startActivity(Intent(activity?.applicationContext,ArtistSettingsActivity::class.java))
        }
    }

    private fun setLinksButtons() {

        if(artist!!.facebookLink!="") {
            fbBtn.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW,Uri.parse(artist!!.facebookLink))
                startActivity(browserIntent)
            }
        } else {
            fbBtn.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.fb_black)
        }


        if(artist!!.youtubeLink !="") {
            ytBtn.setOnClickListener {

                val browserIntent = Intent(Intent.ACTION_VIEW,Uri.parse(artist!!.youtubeLink))
                startActivity(browserIntent)
            }
        } else {
            ytBtn.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.youtube_black)
        }

        if(artist!!.spotifyLink!="") {
            spotiBtn.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW,Uri.parse(artist!!.spotifyLink))
                startActivity(browserIntent)
            }
        } else {
            spotiBtn.background = ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.spotify_black)

        }
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

        if(image!=null) {
            Picasso.get().load(image).into(avatarIV)
        }
        else {
            avatarIV.background =ContextCompat.getDrawable(activity?.applicationContext!!,R.drawable.default_avatar)
        }
    }

    companion object {
        var artist: Artist? = null
        var image: Uri? =  null
    }
}