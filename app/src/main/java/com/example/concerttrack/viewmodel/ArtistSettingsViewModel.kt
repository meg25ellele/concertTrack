package com.example.concerttrack.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.models.MusicGenre
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.repository.StorageRepository
import com.example.concerttrack.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class ArtistSettingsViewModel(application: Application) : AndroidViewModel(application) {

    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }
    private val storageRepository: StorageRepository by lazy { StorageRepository(application) }

    val musicGenresLiveData: MutableLiveData<Resource<MutableList<MusicGenre>>> = MutableLiveData()
    val isNameTaken: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val isDataUpdated: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val successfullyAddedPhotoLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val successfullyDeletedPhotoLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()


    fun retrieveMusicGenres(myGenres: String?) = viewModelScope.launch {
        Log.i("retrieve","retrieve")
        musicGenresLiveData.postValue(Resource.Loading())
        try{
            val musicGenresList = cloudFirestoreRepository.retrieveMusicGenres(myGenres)
            musicGenresLiveData.postValue(musicGenresList)
        } catch(e: Exception) {
            musicGenresLiveData.postValue(Resource.Failure(e))
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

    fun updateArtistData(id: String, newDataMap: Map<String,Any>) = viewModelScope.launch {
        isDataUpdated.postValue(Resource.Loading())
        try {
            val firestoreAnswer = cloudFirestoreRepository.updateArtistData(id,newDataMap)
            isDataUpdated.postValue(firestoreAnswer)
        } catch(e:Exception){
            isDataUpdated.postValue(Resource.Failure(e))
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

    fun deletePhoto(userUID: String) = viewModelScope.launch {
        successfullyDeletedPhotoLiveData.postValue(Resource.Loading())
        try{
            val deletePhotoAnswer = storageRepository.deletePhoto(userUID)
            successfullyDeletedPhotoLiveData.postValue(deletePhotoAnswer)
        } catch (e:Exception) {
            successfullyDeletedPhotoLiveData.postValue(Resource.Failure(e))
        }
    }
}