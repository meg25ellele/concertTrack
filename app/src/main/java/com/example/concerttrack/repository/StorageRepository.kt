package com.example.concerttrack.repository

import android.app.Application
import android.net.Uri
import android.util.Log
import com.example.concerttrack.models.Artist
import com.example.concerttrack.util.Resource
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageRepository(private val application: Application) {

    private var firebaseStorage = FirebaseStorage.getInstance()

    suspend fun addPhotoToStorage(image: Uri, userUID: String): Resource.Success<Boolean> {
        val imageRef = firebaseStorage.reference.child("$userUID.png")
        imageRef.putFile(image).await()
        return Resource.Success(true)
    }

    suspend fun getPhotoFromStorage(userUID: String):Resource<Uri> {
        val image = firebaseStorage.reference.child("$userUID.png").downloadUrl.await()
        return Resource.Success(image)
    }

    suspend fun deletePhoto(userUID: String): Resource<Boolean> {
        firebaseStorage.reference.child("$userUID.png").delete().await()
        return Resource.Success(true)
    }

    suspend fun getArtistPhotos(): Resource<MutableMap<String,Uri>> {
        val imagesMap = mutableMapOf<String,Uri>()

        val images =   firebaseStorage.reference.listAll().await()

        for (image in images.items) {
            val img = image.downloadUrl.await()
            imagesMap.put(image.name,img)
        }

        Log.i("img",imagesMap.toString())
        return Resource.Success(imagesMap)
    }
}