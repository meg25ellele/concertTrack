package com.example.concerttrack.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.MusicGenre
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.repository.StorageRepository
import com.example.concerttrack.util.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.lang.Exception

class ArtistMainPageViewModel(application: Application) : AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }
    private val storageRepository: StorageRepository by lazy { StorageRepository(application) }

    val isUserLoggedOut: MutableLiveData<Boolean> = MutableLiveData()
    var artistLiveData: MutableLiveData<Resource<Artist?>> = MutableLiveData()
    var photoUriLiveData: MutableLiveData<Resource<Uri>> = MutableLiveData()


    fun logOut(){
        val answer = authAppRepository.logOut()
        isUserLoggedOut.postValue(answer.data)
    }

    fun getCurrentUser(): FirebaseUser {
        return authAppRepository.getCurrentUser().data
    }

    fun getArtistData(email: String) = viewModelScope.launch {
        artistLiveData.postValue(Resource.Loading())
        try {
            val firebaseAnswer = cloudFirestoreRepository.getArtist(email)
            artistLiveData.postValue(firebaseAnswer)
        } catch(e:Exception) {
            artistLiveData.postValue(Resource.Failure(e))
        }
    }

    fun getPhotoFromStorage(userUID: String) = viewModelScope.launch {
        photoUriLiveData.postValue(Resource.Loading())
        try {
            val firebaseResource = storageRepository.getPhotoFromStorage(userUID)
            photoUriLiveData.postValue(firebaseResource)
        } catch(e:Exception) {
            photoUriLiveData.postValue(Resource.Failure(e))
        }
    }




}