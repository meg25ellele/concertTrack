package com.example.concerttrack.ui.artist_register_fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.concerttrack.R
import com.example.concerttrack.util.Constants.Companion
import com.example.concerttrack.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.example.concerttrack.util.Constants.Companion.PICK_IMAGE_REQUEST
import com.example.concerttrack.util.Resource
import com.example.concerttrack.viewmodel.ArtistRegisterViewModel
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_artist_register_second.*
import java.lang.StringBuilder

class ArtistRegisterSecondFragment: Fragment(R.layout.fragment_artist_register_second) {

    private val artistRegisterViewModel: ArtistRegisterViewModel by lazy { ViewModelProvider(this).get(
        ArtistRegisterViewModel::class.java) }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backBtn.setOnClickListener {
            view.findNavController().popBackStack()
        }

        addAvatarBtn.setOnClickListener {
            loadPhotoFromGallery()
        }

        artistRegisterViewModel.retrieveMusicGenresLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    val musicGenresList = it.data
                    val sb = StringBuilder()

                    for (genre in musicGenresList) {
                        sb.append(genre.name)
                        sb.append(" ")
                    }
                    musicGenresListTV.text = sb.toString()
                }

                is Resource.Failure-> {

                }
            }
        })

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
            };
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

    companion object{
        private var mImageUri: Uri? = null
    }
}