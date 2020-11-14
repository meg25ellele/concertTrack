package com.example.concerttrack.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.concerttrack.R
import com.example.concerttrack.adapters.GenresAdapter
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.MusicGenre
import com.example.concerttrack.util.*
import com.example.concerttrack.viewmodel.ArtistSettingsViewModel
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_artist_settings.*
import kotlinx.android.synthetic.main.activity_artist_settings.addAvatarBtn
import kotlinx.android.synthetic.main.activity_artist_settings.avatarIV
import kotlinx.android.synthetic.main.activity_artist_settings.deleteAvatarBtn
import kotlinx.android.synthetic.main.activity_artist_settings.fbLink
import kotlinx.android.synthetic.main.activity_artist_settings.musicGenresRV
import kotlinx.android.synthetic.main.activity_artist_settings.progressBar
import kotlinx.android.synthetic.main.activity_artist_settings.shortDesc
import kotlinx.android.synthetic.main.activity_artist_settings.spotiLink
import kotlinx.android.synthetic.main.activity_artist_settings.text_input_userName
import kotlinx.android.synthetic.main.activity_artist_settings.userName
import kotlinx.android.synthetic.main.activity_artist_settings.ytLink


class ArtistSettingsActivity : AppCompatActivity() {

    private val artistSettingsViewModel: ArtistSettingsViewModel by lazy {
        ViewModelProvider(this).get(ArtistSettingsViewModel::class.java) }

    val musicGenreList  = mutableListOf<MusicGenre>()
    var myGenresString: String? = null

    private var allControls: List<View> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_settings)

        loadData()

        artistSettingsViewModel.retrieveMusicGenres(myGenresString)

        allControls = listOf(text_input_userName,addAvatarBtn,shortDesc,fbLink,ytLink,spotiLink,musicGenresRV,deleteAvatarBtn,saveChangesBtn)

        artistSettingsViewModel.musicGenresLiveData.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    for(genre in it.data){
                        musicGenreList.add(genre)
                    }
                    setAdapterAndManager()
                }
                is Resource.Failure-> {
                }
            }
        })

        artistSettingsViewModel.isNameTaken.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    if(!it.data) {
                        artistSettingsViewModel.updateArtistData(artist!!.id,getNewData())
                    } else {
                        hideSpinnerAndEnableControls()
                        text_input_userName.error = getString(R.string.duplicatedArtistName)
                    }
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    this.showToastError(R.string.unknownUpdatingArtistError)
                }
            }
        })

        artistSettingsViewModel.isDataUpdated.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    if(it.data) {
                        if(currentImage!=null && currentImage.toString()!= image.toString()) {
                            artistSettingsViewModel.addPhotoToStorage(currentImage!!, artist!!.id)
                        }
                        else if (currentImage==null && image!=null) {
                            artistSettingsViewModel.deletePhoto(artist!!.id)
                        } else{

                            this.showToastSuccess(R.string.successUpdatingData)
                            hideSpinnerAndEnableControls()
                            startActivity(Intent(this,ArtistMainPageActivity::class.java))
                            finish()
                        }
                    }
                    else {
                        hideSpinnerAndEnableControls()
                        this.showToastError(R.string.unknownUpdatingArtistError)
                    }
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    this.showToastError(R.string.unknownUpdatingArtistError)
                }
            }
        })

        artistSettingsViewModel.successfullyAddedPhotoLiveData.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    this.showToastSuccess(R.string.successUpdatingData)
                    hideSpinnerAndEnableControls()
                    finish()
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    this.showToastError(R.string.unknownUpdatingArtistError)
                }
            }
        })


        artistSettingsViewModel.successfullyDeletedPhotoLiveData.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    this.showToastSuccess(R.string.successUpdatingData)
                    hideSpinnerAndEnableControls()
                    finish()
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    this.showToastError(R.string.unknownUpdatingArtistError)
                }
            }
        })

        exitBtn.setOnClickListener {
            finish()
        }

        deleteAvatarBtn.setOnClickListener {
            currentImage = null
            avatarIV.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.default_avatar))
        }

        addAvatarBtn.setOnClickListener {
            loadPhotoFromGallery()
        }

        saveChangesBtn.setOnClickListener {
            if(isDataEdited()) {
                if(validateUserName() && userName.text.toString() != artist?.name) {
                    artistSettingsViewModel.alreadyExists(userName.text.toString())
                }
                else {
                    artistSettingsViewModel.updateArtistData(artist!!.id,getNewData())
                }
            }
            else {
                text_input_userName.error = null
                this.showToastInfo(R.string.nothingToEdit)
            }
        }

    }

    private fun validateUserName(): Boolean {
        val isEmpty: Boolean = text_input_userName.editText?.content()?.isEmpty() ?: true

        return when {
            isEmpty -> {
                text_input_userName.error = getString(R.string.notAllowedEmptyField)
                false
            }
            else -> {
                text_input_userName.error = null
                true
            }
        }
    }

    private fun getNewData(): Map<String,Any> {

        val name = userName.text.toString()
        val description = shortDesc.text.toString()
        val facebookLink = fbLink.text.toString()
        val youtubeLink =  ytLink.text.toString()
        val spotifyLink = spotiLink.text.toString()
        val myGenres = getMusicGenres()

        val map = mutableMapOf<String,Any>()

        if(name!= artist?.name) {
            map["name"] = name
        }
        if(description!=artist?.description) {
            map["description"] = description
        }
        if(facebookLink!=artist?.facebookLink) {
            map["facebookLink"] = facebookLink
        }
        if(youtubeLink!=artist?.youtubeLink) {
            map["youtubeLink"] = youtubeLink
        }
        if(spotifyLink!=artist?.spotifyLink) {
            map["spotifyLink"] = spotifyLink
        }
        if(myGenres!= artist?.myGenres) {
            map["myGenres"] = myGenres
        }
        return map
    }

    private fun isDataEdited():Boolean {
        return userName.text.toString() != artist?.name ||
                shortDesc.text.toString() != artist?.description ||
                fbLink.text.toString() != artist?.facebookLink ||
                ytLink.text.toString() != artist?.youtubeLink ||
                spotiLink.text.toString() != artist?.spotifyLink ||
                currentImage.toString() != image.toString() ||
                getMusicGenres() != artist?.myGenres
    }

    private fun getMusicGenres() : List<String>{
        var myGenres = mutableListOf<String>()
        for(genre in musicGenreList) {
            if(genre.chosen) {
                myGenres.add(genre.name)
            }
        }
        return myGenres
    }

    private fun loadPhotoFromGallery(){

        if(this.let { PermissionChecker.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) } == PackageManager.PERMISSION_DENIED) {
            val permission= arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permission, Constants.GALLERY_REQUEST_CODE)
        } else {
            openGallery()
        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.PICK_IMAGE_REQUEST);
    }


    private fun loadData() {
        if(image!=null) {
            Picasso.get().load(image).into(avatarIV)
            currentImage = image
        }

        userName.setText(artist?.name)
        shortDesc.setText(artist?.description)
        fbLink.setText(artist?.facebookLink)
        ytLink.setText(artist?.youtubeLink)
        spotiLink.setText(artist?.spotifyLink)

        if(artist?.myGenres!=null) {
            for(genre in artist?.myGenres!!) {
                myGenresString+=genre
                myGenresString+=" "
            }
        }
    }

    private fun setAdapterAndManager(){
        musicGenresRV.adapter = GenresAdapter(musicGenreList)
        musicGenresRV.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
    }

    private fun showSpinnerAndDisableControls() {
        progressBar.visibility = View.VISIBLE
        allControls.forEach { v -> v.isEnabled = false }
    }

    private  fun hideSpinnerAndEnableControls() {
        progressBar.visibility = View.GONE
        allControls.forEach { v -> v.isEnabled = true }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== Constants.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data !=null && data.data != null){

            currentImage = data.data


            CropImage.activity(currentImage)
                .setAspectRatio(1,1)
                .setAutoZoomEnabled(false)
                .start(this)

        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data!=null) {
            val result : CropImage.ActivityResult = CropImage.getActivityResult(data)
            if(resultCode == Activity.RESULT_OK) {
                currentImage = result.uri
                Picasso.get().load(currentImage).into(avatarIV)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            Constants.GALLERY_REQUEST_CODE -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openGallery()
            else -> Toast.makeText(this,getString(R.string.permission_denied_info), Toast.LENGTH_SHORT).show()

        }
    }

    companion object {
        var currentImage: Uri? = null
        var artist: Artist? = null
        var image: Uri? = null
    }
}