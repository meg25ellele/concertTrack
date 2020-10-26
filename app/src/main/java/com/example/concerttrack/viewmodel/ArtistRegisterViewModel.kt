package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.concerttrack.models.MusicGenre
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.repository.FirestoreRepository
import com.example.concerttrack.util.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ArtistRegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    private  val firestoreRepository: FirestoreRepository by lazy { FirestoreRepository(application) }


    val musicGenresLiveData: MutableLiveData<Resource<MutableList<MusicGenre>>> = MutableLiveData()
    val successfullyRegisterLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    fun registerUser(email:String, password: String, name: String) = viewModelScope.launch {
        successfullyRegisterLiveData.postValue(Resource.Loading())
        try {
            val answer = authAppRepository.registerUser(email,password,name)
            successfullyRegisterLiveData.postValue(answer)
        } catch (e: Exception) {
            successfullyRegisterLiveData.postValue(Resource.Failure(e))
        }

    }

    fun retrieveMusicGenres() = viewModelScope.launch {
        musicGenresLiveData.postValue(Resource.Loading())
        try{
            val musicGenresList = firestoreRepository.retrieveMusicGenres()
            musicGenresLiveData.postValue(musicGenresList)
        } catch(e:Exception) {
            musicGenresLiveData.postValue(Resource.Failure(e))
        }
    }

}