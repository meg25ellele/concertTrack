package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.models.Event
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class FanInterestedEventsViewModel(application: Application) : AndroidViewModel(application) {

    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }

    val successfullyAddedEventToInterested: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val successfullyRemovedEventFromInterested: MutableLiveData<Resource<Boolean>> = MutableLiveData()

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
}