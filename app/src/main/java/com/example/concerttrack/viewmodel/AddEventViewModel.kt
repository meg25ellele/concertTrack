package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.models.Event
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.util.Resource
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import java.lang.Exception

class AddEventViewModel(application: Application) : AndroidViewModel(application)  {

    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }

    val successfullyAddedEvent: MutableLiveData<Resource<Boolean>> = MutableLiveData()


    fun addNewEvent(header: String, startDateTime:String,
                    shortDescription: String, ticketsLink: String, artistReferencePath: String,
                    placeName: String, placeAddress: String, placeLat: Double, placeLng: Double)
                    = viewModelScope.launch {
        successfullyAddedEvent.postValue(Resource.Loading())
        try {
            val newEvent = Event(header,startDateTime,shortDescription,
                                ticketsLink, placeName, placeAddress, placeLat,placeLng, artistReferencePath)
            val firestoreAnswer = cloudFirestoreRepository.addNewEvent(newEvent)
            successfullyAddedEvent.postValue(firestoreAnswer)

        } catch(e: Exception) {
            successfullyAddedEvent.postValue(Resource.Failure(e))
        }

    }
}