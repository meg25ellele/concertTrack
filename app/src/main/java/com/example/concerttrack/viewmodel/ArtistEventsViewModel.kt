package com.example.concerttrack.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.models.Event
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.Constants.Companion.DATE_TIME_FORMAT
import com.example.concerttrack.util.Constants.Companion.FIREBASE_EXCEPTION_TAG
import com.example.concerttrack.util.Resource
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.ZonedDateTime

class ArtistEventsViewModel(application: Application) : AndroidViewModel(application) {

    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }


    fun retrieveArtistComingEvents(artist: DocumentReference) = viewModelScope.launch {
        artistComingEventsLiveData.postValue(Resource.Loading())
        try{
            val artistComingEvents = cloudFirestoreRepository.retrieveArtistComingEvents(artist).data
            artistComingEventsLiveData.postValue(Resource.Success(artistComingEvents))
        }catch(e:Exception) {
            artistComingEventsLiveData.postValue(Resource.Failure(e))
        }
    }

    fun retrieveArtistPastEvents(artist: DocumentReference) = viewModelScope.launch {
        artistPastEventsLiveData.postValue(Resource.Loading())
        try{
            val artistComingEvents = cloudFirestoreRepository.retrieveArtistPastEvents(artist).data
            artistPastEventsLiveData.postValue(Resource.Success(artistComingEvents))
        }catch(e:Exception) {
            artistPastEventsLiveData.postValue(Resource.Failure(e))
        }
    }

    fun deleteArtistEvent(event: Event) = viewModelScope.launch {
        try{
            cloudFirestoreRepository.deleteEvent(event)
        } catch(e: Exception) {
            Log.i(FIREBASE_EXCEPTION_TAG,e.message)
        }
    }

    fun addArtistEvent(event:Event) = viewModelScope.launch {
        try {
            cloudFirestoreRepository.addNewEvent(event)
        } catch(e: Exception) {
            Log.i(FIREBASE_EXCEPTION_TAG,e.message)
        }
    }

    companion object {
        val artistComingEventsLiveData: MutableLiveData<Resource<MutableList<Event>>> = MutableLiveData()
        val artistPastEventsLiveData: MutableLiveData<Resource<MutableList<Event>>> = MutableLiveData()

    }

}