package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.concerttrack.models.MusicGenre
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.repository.FirestoreRepository
import com.example.concerttrack.util.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class ArtistRegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    private  val firestoreRepository: FirestoreRepository by lazy { FirestoreRepository(application) }

    var userLiveData: MutableLiveData<FirebaseUser>? = null
    var isRegisterSuccessful: MutableLiveData<Boolean>? = null


    val retrieveMusicGenresLiveData: LiveData<Resource<MutableList<MusicGenre>>> = liveData(Dispatchers.IO){
        try{
            val musicGenresList = firestoreRepository.retrieveMusicGenres()
            emit(musicGenresList)
        } catch (e:Exception) {
            emit(Resource.Failure(e.cause!!))
        }
    }




    init {
        userLiveData = authAppRepository.userLiveData
        isRegisterSuccessful = authAppRepository.isRegisterSuccessful
    }

    fun register(email: String, password: String, name: String) {
        authAppRepository.registerUser1(email,password, name)
    }


}