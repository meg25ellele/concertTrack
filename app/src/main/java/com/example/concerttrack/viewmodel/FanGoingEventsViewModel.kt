package com.example.concerttrack.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Event
import com.example.concerttrack.models.Fan
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.repository.StorageRepository
import com.example.concerttrack.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class FanGoingEventsViewModel(application: Application) : AndroidViewModel(application) {

    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }
    private val storageRepository: StorageRepository by lazy { StorageRepository(application) }


    val successfullyAddedEventToMine: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val successfullyRemovedEventFromMine: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    val fanEventsLiveData: MutableLiveData<Resource<MutableList<Event>>> = MutableLiveData()
    val artistsMap: MutableLiveData<Resource<Map<String, Artist>>> = MutableLiveData()
    val imagesMap: MutableLiveData<Resource<Map<String, Uri>>> = MutableLiveData()

    fun addEventToMine(fanID: String,event: Event) = viewModelScope.launch {
        successfullyAddedEventToMine.postValue(Resource.Loading())
        try{
            val firestoreAnswer = cloudFirestoreRepository.addEventToMine(fanID,event)
            successfullyAddedEventToMine.postValue(firestoreAnswer)
        } catch(e: Exception) {
            successfullyAddedEventToMine.postValue(Resource.Failure(e))
        }
    }

    fun removeEventFromMine(fanID: String,event: Event) = viewModelScope.launch {
        successfullyRemovedEventFromMine.postValue(Resource.Loading())
        try{
            val firestoreAnswer = cloudFirestoreRepository.removeEventFromMine(fanID,event)
            successfullyRemovedEventFromMine.postValue(firestoreAnswer)
        } catch(e: Exception) {
            successfullyRemovedEventFromMine.postValue(Resource.Failure(e))
        }
    }

    fun retrieveFanEvents(fanID: String) = viewModelScope.launch {
        fanEventsLiveData.postValue(Resource.Loading())
        ArtistEventsViewModel.artistComingEventsLiveData.postValue(Resource.Loading())
        try{
            val artistComingEvents = cloudFirestoreRepository.retrieveFanEvents(fanID).data
            fanEventsLiveData.postValue(Resource.Success(artistComingEvents))
            ArtistEventsViewModel.artistComingEventsLiveData.postValue(Resource.Success(artistComingEvents))

        }catch(e:Exception) {
            fanEventsLiveData.postValue(Resource.Failure(e))
            ArtistEventsViewModel.artistComingEventsLiveData.postValue(Resource.Failure(e))
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