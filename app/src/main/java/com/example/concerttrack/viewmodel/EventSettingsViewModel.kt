package com.example.concerttrack.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.models.Event
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class EventSettingsViewModel(application: Application) : AndroidViewModel(application)  {

    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }

    val successfullyUpdatedEvent: MutableLiveData<Resource<Boolean>> = MutableLiveData()


    fun deleteArtistEvent(event: Event) = viewModelScope.launch {
        try{
            cloudFirestoreRepository.deleteEvent(event)
        } catch(e: Exception) {
            Log.i(Constants.FIREBASE_EXCEPTION_TAG,e.message)
        }
    }

    fun updateEventData(oldEvent: Event,newEvent:Event) = viewModelScope.launch {
        successfullyUpdatedEvent.postValue(Resource.Loading())
        try{
            val firestoreAnswer = cloudFirestoreRepository.updateEventData(oldEvent,newEvent)
            successfullyUpdatedEvent.postValue(firestoreAnswer)
        } catch(e:Exception){
            successfullyUpdatedEvent.postValue(Resource.Failure(e))
        }
    }
}