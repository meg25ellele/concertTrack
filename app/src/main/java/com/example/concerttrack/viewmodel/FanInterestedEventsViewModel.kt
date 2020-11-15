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
import kotlinx.coroutines.launch
import java.lang.Exception

class FanInterestedEventsViewModel(application: Application) : AndroidViewModel(application) {

    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }
    private val storageRepository: StorageRepository by lazy { StorageRepository(application) }


    val successfullyAddedEventToInterested: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val successfullyRemovedEventFromInterested: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    val fanInterestedEventsLiveData: MutableLiveData<Resource<MutableList<Event>>> = MutableLiveData()
    val artistsMap: MutableLiveData<Resource<Map<String, Artist>>> = MutableLiveData()
    val imagesMap: MutableLiveData<Resource<Map<String, Uri>>> = MutableLiveData()


    fun addEventToInterested(fanID: String,event: Event) = viewModelScope.launch {
        successfullyAddedEventToInterested.postValue(Resource.Loading())
        try{
            val firestoreAnswer = cloudFirestoreRepository.addEventToInterested(fanID,event)
            successfullyAddedEventToInterested.postValue(firestoreAnswer)
        } catch(e: Exception) {
            successfullyAddedEventToInterested.postValue(Resource.Failure(e))
        }
    }

    fun removeEventFromInterested(fanID: String,event: Event) = viewModelScope.launch {
        successfullyRemovedEventFromInterested.postValue(Resource.Loading())
        try{
            val firestoreAnswer = cloudFirestoreRepository.removeEventFromInterested(fanID,event)
            successfullyRemovedEventFromInterested.postValue(firestoreAnswer)
        } catch(e: Exception) {
            successfullyRemovedEventFromInterested.postValue(Resource.Failure(e))
        }
    }

    fun retrieveInterestedEvents(fanID: String) = viewModelScope.launch {
        fanInterestedEventsLiveData.postValue(Resource.Loading())
        ArtistEventsViewModel.artistComingEventsLiveData.postValue(Resource.Loading())
        try{
            val artistComingEvents = cloudFirestoreRepository.retrieveInterestedEvents(fanID).data
            fanInterestedEventsLiveData.postValue(Resource.Success(artistComingEvents))
            ArtistEventsViewModel.artistComingEventsLiveData.postValue(Resource.Success(artistComingEvents))

        }catch(e:Exception) {
            fanInterestedEventsLiveData.postValue(Resource.Failure(e))
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