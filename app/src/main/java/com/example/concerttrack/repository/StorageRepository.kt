package com.example.concerttrack.repository

import android.app.Application
import android.net.Uri
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
}