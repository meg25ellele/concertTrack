package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.models.Event
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.Resource
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.ZonedDateTime

class ArtistEventsViewModel(application: Application) : AndroidViewModel(application) {

    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }


    fun retrieveArtistEvents(artist: DocumentReference) = viewModelScope.launch {
        artistEventsLiveData.postValue(Resource.Loading())
        try{
            val artistEvents = cloudFirestoreRepository.retrieveArtistEvents(artist).data
            //split events to coming and past
            val comingEventsList  = mutableListOf<Event>()
            val pastEventsList = mutableListOf<Event>()

            for(event in artistEvents) {
                val dateAndTime = event.startDate + " " + event.startTime
                val parsedDate = ZonedDateTime.parse(dateAndTime, Constants.DATE_TIME_FORMATTER)

                if(parsedDate.isBefore(ZonedDateTime.now())) {
                    pastEventsList.add(event)
                } else {
                    comingEventsList.add(event)
                }
            }
            artistEventsLiveData.postValue(Resource.Success(Pair(comingEventsList,pastEventsList)))
        }catch(e:Exception) {
            artistEventsLiveData.postValue(Resource.Failure(e))
        }
    }

    companion object {
        val artistEventsLiveData: MutableLiveData<Resource<Pair<MutableList<Event>,MutableList<Event>>>> = MutableLiveData()

    }

}