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
}