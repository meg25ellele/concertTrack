package com.example.concerttrack.ui.artistRegisterFragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.concerttrack.R
import com.example.concerttrack.adapters.GenresAdapter
import com.example.concerttrack.models.MusicGenre
import com.example.concerttrack.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.example.concerttrack.util.Constants.Companion.PICK_IMAGE_REQUEST
import com.example.concerttrack.util.Resource
import com.example.concerttrack.util.showToastError
import com.example.concerttrack.util.showToastSuccess
import com.example.concerttrack.viewmodel.ArtistRegisterViewModel
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_artist_register_second.*
import kotlinx.android.synthetic.main.fragment_artist_register_second.progressBar
import kotlinx.android.synthetic.main.fragment_artist_register_second.registerBtn


class ArtistRegisterSecondFragment: Fragment(R.layout.fragment_artist_register_second) {

    private val artistRegisterViewModel: ArtistRegisterViewModel by lazy { ViewModelProvider(this).get(
        ArtistRegisterViewModel::class.java) }

    private var allControls: List<View> = listOf()

    val musicGenreList  = mutableListOf<MusicGenre>()

    lateinit var name: String
    lateinit var email:String
    lateinit var password: String



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        name = arguments?.getString(getString(R.string.name)).toString()
        email = arguments?.getString(getString(R.string.email)).toString()
        password = arguments?.getString(getString(R.string.password)).toString()

        artistRegisterViewModel.retrieveMusicGenres()

        allControls = listOf(addAvatarBtn,shortDesc,fbLink,ytLink,spotiLink,musicGenresRV,registerBtn)


        backBtn.setOnClickListener {
            view.findNavController().popBackStack()
        }

        addAvatarBtn.setOnClickListener {
            loadPhotoFromGallery()
        }

        registerBtn.setOnClickListener {
            artistRegisterViewModel.registerUser(email,password,name)
        }

        artistRegisterViewModel.musicGenresLiveData.observe(viewLifecycleOwner, Observer {
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


        artistRegisterViewModel.registerUIDLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {

                    val myGenres = getMusicGenres()
                    val fbLink =  if (fbLink.text.toString() != "") fbLink.text.toString() else null
                    val ytLink = if (ytLink.text.toString() != "") ytLink.text.toString() else null
                    val spotiLink = if (spotiLink.text.toString() != "") spotiLink.text.toString() else null
                    val description = if(shortDesc.text.toString() !="") shortDesc.text.toString() else null

                    artistRegisterViewModel.addNewArtist(it.data,email,name,
                        description,fbLink,
                        ytLink,spotiLink,myGenres)

                    if(mImageUri!=null){
                        artistRegisterViewModel.addPhotoToStorage(mImageUri!!,it.data)
                    }
                    activity?.finish()
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()

                    when(it.throwable.javaClass.simpleName) {
                        "FirebaseAuthUserCollisionException" -> {
                            this.showToastError(R.string.accountExists)
                        }
                        else -> {
                            Log.e("error",it.throwable.message)
                            this.showToastError(R.string.unknownRegisterError)
                        }
                    }
                }
            }
        })

        artistRegisterViewModel.successfullyAddedArtist.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    hideSpinnerAndEnableControls()
                    this.showToastSuccess(R.string.registerSuccess)
                    activity?.finish()
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    this.showToastError(R.string.unknownRegisterError)
                }
            }
        })
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("saved","saved")
    }

    private fun loadPhotoFromGallery(){

        if(context?.let { PermissionChecker.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) } == PackageManager.PERMISSION_DENIED) {
            val permission= arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permission, GALLERY_REQUEST_CODE)
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data !=null && data.data != null){

            mImageUri = data.data

            context?.let {
                CropImage.activity(mImageUri)
                    .setAspectRatio(1,1)
                    .setAutoZoomEnabled(false)
                    .start(requireContext(),this)
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data!=null) {
            val result : CropImage.ActivityResult = CropImage.getActivityResult(data)
            if(resultCode == Activity.RESULT_OK) {
                mImageUri = result.uri
                Picasso.get().load(mImageUri).into(avatarIV)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            GALLERY_REQUEST_CODE -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openGallery()
            else -> Toast.makeText(activity,getString(R.string.permission_denied_info), Toast.LENGTH_SHORT).show()

        }
    }

    private fun setAdapterAndManager(){
        musicGenresRV.adapter = GenresAdapter(musicGenreList)
        musicGenresRV.layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.HORIZONTAL)
    }

    private fun showSpinnerAndDisableControls() {
        progressBar.visibility = View.VISIBLE
        allControls.forEach { v -> v.isEnabled = false }
    }

    private  fun hideSpinnerAndEnableControls() {
        progressBar.visibility = View.GONE
        allControls.forEach { v -> v.isEnabled = true }
    }



    companion object{
         var mImageUri: Uri? = null
    }
}