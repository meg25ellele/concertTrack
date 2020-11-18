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

class FanFavouritesArtistsViewModel(application: Application) : AndroidViewModel(application) {

    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }
    private val storageRepository: StorageRepository by lazy { StorageRepository(application) }

    val successfullyAddedArtistToFavourites: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val successfullyRemovedArtistFromFavourites: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    val fanFavouritesArtistsLiveData: MutableLiveData<Resource<MutableList<Artist>>> = MutableLiveData()
    val imagesMap: MutableLiveData<Resource<Map<String, Uri>>> = MutableLiveData()

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

    fun retrieveFavouritesArtists(fanID: String) = viewModelScope.launch {
        fanFavouritesArtistsLiveData.postValue(Resource.Loading())
        try{
            val artistComingEvents = cloudFirestoreRepository.retrieveFavouritesArtists(fanID).data
            fanFavouritesArtistsLiveData.postValue(Resource.Success(artistComingEvents))
        }catch(e:Exception) {
            fanFavouritesArtistsLiveData.postValue(Resource.Failure(e))
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