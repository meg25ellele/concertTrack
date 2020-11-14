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

class FanFavouritesArtistsViewModel(application: Application) : AndroidViewModel(application) {

    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }

    val successfullyAddedArtistToFavourites: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val successfullyRemovedArtistFromFavourites: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    fun addArtistToFavourites(fanID: String,artistID:String) = viewModelScope.launch {
        successfullyAddedArtistToFavourites.postValue(Resource.Loading())
        try{
            val firestoreAnswer = cloudFirestoreRepository.addArtistToFavourites(fanID,artistID)
            successfullyAddedArtistToFavourites.postValue(firestoreAnswer)
        } catch(e:Exception) {
            successfullyAddedArtistToFavourites.postValue(Resource.Failure(e))
        }
    }

    fun removeArtistFromFavourites(fanID: String,artistID:String) = viewModelScope.launch {
        successfullyRemovedArtistFromFavourites.postValue(Resource.Loading())
        try{
            val firestoreAnswer = cloudFirestoreRepository.removeArtistFromFavourites(fanID,artistID)
            successfullyRemovedArtistFromFavourites.postValue(firestoreAnswer)
        } catch(e:Exception) {
            successfullyRemovedArtistFromFavourites.postValue(Resource.Failure(e))
        }
    }

}