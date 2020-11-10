package com.example.concerttrack.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.repository.StorageRepository
import com.example.concerttrack.util.Resource
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.launch
import java.lang.Exception

class EventsViewModel(application: Application) : AndroidViewModel(application) {

    private val storageRepository: StorageRepository by lazy { StorageRepository(application) }
    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }

    val comingEventsLiveData: MutableLiveData<Resource<MutableList<Event>>> = MutableLiveData()
    val pastEventsLiveData: MutableLiveData<Resource<MutableList<Event>>> = MutableLiveData()
    val artistsMap: MutableLiveData<Resource<Map<String, Artist>>> = MutableLiveData()
    val imagesMap: MutableLiveData<Resource<Map<String,Uri>>> = MutableLiveData()

    fun retrieveComingEvents() = viewModelScope.launch {
        comingEventsLiveData.postValue(Resource.Loading())
        try{
            val artistComingEvents = cloudFirestoreRepository.retrieveComingEvents().data
            comingEventsLiveData.postValue(Resource.Success(artistComingEvents))
        }catch(e: Exception) {
            comingEventsLiveData.postValue(Resource.Failure(e))
        }
    }

    fun retrieveArtistPastEvents() = viewModelScope.launch {
        pastEventsLiveData.postValue(Resource.Loading())
        try{
            val artistComingEvents = cloudFirestoreRepository.retrievePastEvents().data
            pastEventsLiveData.postValue(Resource.Success(artistComingEvents))
        }catch(e: Exception) {
            pastEventsLiveData.postValue(Resource.Failure(e))
        }
    }

    fun getArtistsMap() = viewModelScope.launch {
        artistsMap.postValue(Resource.Loading())
        try{
            val artistsMapData = cloudFirestoreRepository.getArtistsMap()
            artistsMap.postValue(artistsMapData)

        } catch (e: Exception) {
            artistsMap.postValue(Resource.Failure(e))
        }
    }

    fun getArtistPhotos() = viewModelScope.launch {
        imagesMap.postValue(Resource.Loading())
        try{
            val imagesData = storageRepository.getArtistPhotos()
            imagesMap.postValue(imagesData)
        }catch(e:Exception) {
            imagesMap.postValue(Resource.Failure(e))
        }
    }
}