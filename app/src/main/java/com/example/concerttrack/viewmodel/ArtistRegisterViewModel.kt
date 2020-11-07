package com.example.concerttrack.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.MusicGenre
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.repository.StorageRepository
import com.example.concerttrack.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class ArtistRegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }
    private val storageRepository: StorageRepository by lazy {StorageRepository(application)}


    val musicGenresLiveData: MutableLiveData<Resource<MutableList<MusicGenre>>> = MutableLiveData()
    val registerUIDLiveData: MutableLiveData<Resource<String>> = MutableLiveData()
    val successfullyAddedPhotoLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val successfullyAddedArtist: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val isNameTaken: MutableLiveData<Resource<Boolean>> = MutableLiveData()



    fun registerUser(email:String, password: String, name: String) = viewModelScope.launch {
        registerUIDLiveData.postValue(Resource.Loading())
        try {
            val registerAnswer = authAppRepository.registerUser(email,password)
            registerUIDLiveData.postValue(registerAnswer)
        } catch (e: Exception) {
            registerUIDLiveData.postValue(Resource.Failure(e))
        }
    }

    fun retrieveMusicGenres() = viewModelScope.launch {
        musicGenresLiveData.postValue(Resource.Loading())
        try{
            val musicGenresList = cloudFirestoreRepository.retrieveMusicGenres()
            musicGenresLiveData.postValue(musicGenresList)
        } catch(e:Exception) {
            musicGenresLiveData.postValue(Resource.Failure(e))
        }
    }

    fun addPhotoToStorage(image: Uri, userUID:String) = viewModelScope.launch {
        successfullyAddedPhotoLiveData.postValue(Resource.Loading())
        try{
            val uploadPhotoAnswer = storageRepository.addPhotoToStorage(image,userUID)
            successfullyAddedPhotoLiveData.postValue(uploadPhotoAnswer)
        } catch (e:Exception) {
            successfullyAddedPhotoLiveData.postValue(Resource.Failure(e))
        }
    }

    fun addNewArtist(id:String, email:String, name:String,description:String,
                     facebookLink:String, youtubeLink:String,spotifyLink:String,
                     myGenres:List<String>?) = viewModelScope.launch {
        successfullyAddedArtist.postValue(Resource.Loading())
        try{
            val newArtist =
                Artist(id, email, name, description, facebookLink, youtubeLink, spotifyLink,myGenres)
            val firestoreAnswer =
                cloudFirestoreRepository.addNewArtist(newArtist)
            successfullyAddedArtist.postValue(firestoreAnswer)
        } catch (e:Exception) {
            successfullyAddedArtist.postValue(Resource.Failure(e))
        }
    }

    fun alreadyExists(name: String) = viewModelScope.launch {
        isNameTaken.postValue(Resource.Loading())
        try{
            val firestoreAnswer = cloudFirestoreRepository.findArtistByName(name)
            isNameTaken.postValue(firestoreAnswer)
        } catch (e:Exception) {
            isNameTaken.postValue(Resource.Failure(e))
        }
    }

}