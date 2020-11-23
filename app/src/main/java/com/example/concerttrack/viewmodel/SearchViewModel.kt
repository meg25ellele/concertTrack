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

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val storageRepository: StorageRepository by lazy { StorageRepository(application) }
    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }

    val eventsResultsMap: MutableLiveData<Resource<Map<String,Any?>>> = MutableLiveData()
    val favouritesAndRecommendedArtistsMap: MutableLiveData<Resource<Map<String,Any?>>> = MutableLiveData()
    val recommendedComingEventsResultsMap: MutableLiveData<Resource<List<Event>>> = MutableLiveData()
    val artistsResultMap: MutableLiveData<Resource<Map<String,Any>>> = MutableLiveData()
    val hasFavouritesArtistsLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    val artistsMap: MutableLiveData<Resource<Map<String, Artist>>> = MutableLiveData()
    val imagesMap: MutableLiveData<Resource<Map<String, Uri>>> = MutableLiveData()

    fun searchForArtists(word: String) = viewModelScope.launch {
        artistsResultMap.postValue(Resource.Loading())
        try{
            val firestoreAnswer = cloudFirestoreRepository.searchForArtists(word)
            artistsResultMap.postValue(firestoreAnswer)
        } catch(e: Exception) {
            artistsResultMap.postValue(Resource.Failure(e))
        }
    }

    fun searchForEvents(word: String, artistsIDList: List<String>) = viewModelScope.launch {
        eventsResultsMap.postValue(Resource.Loading())
        try{
            val firestoreAnswer = cloudFirestoreRepository.searchForEvents(word,artistsIDList)
            eventsResultsMap.postValue(firestoreAnswer)
        } catch(e: Exception) {
            eventsResultsMap.postValue(Resource.Failure(e))
        }
    }

    fun getArtistPhotos() = viewModelScope.launch {
        imagesMap.postValue(Resource.Loading())
        try{
            val imagesData = storageRepository.getArtistPhotos()
            imagesMap.postValue(imagesData)
        }catch(e: Exception) {
            imagesMap.postValue(Resource.Failure(e))
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

    fun hasFanFavouritesArtists(fanID: String) = viewModelScope.launch {
        hasFavouritesArtistsLiveData.postValue(Resource.Loading())
        try{
            val firebaseAnswer = cloudFirestoreRepository.hasFanFavouritesArtists(fanID)
            hasFavouritesArtistsLiveData.postValue(firebaseAnswer)
        } catch(e:Exception) {
            hasFavouritesArtistsLiveData.postValue(Resource.Failure(e))
        }
    }

    fun getFavouriteAndRecommendedArtists(fanID: String) = viewModelScope.launch {
        favouritesAndRecommendedArtistsMap.postValue(Resource.Loading())
        try{
            val firebaseAnswer = cloudFirestoreRepository.getFavouriteAndRecommendedArtists(fanID)
            favouritesAndRecommendedArtistsMap.postValue(firebaseAnswer)
        } catch(e:Exception) {
            favouritesAndRecommendedArtistsMap.postValue(Resource.Failure(e))
        }
    }

    fun getRecommendedComingEvents(favArtistsMap: Map<String,
            Artist>,recommendedArtistsMap: Map<String,Artist>) = viewModelScope.launch {
        recommendedComingEventsResultsMap.postValue(Resource.Loading())
        try {
            val firebaseAnswer = cloudFirestoreRepository.getRecommendedEvents(favArtistsMap,
                                                                                                    recommendedArtistsMap)
            recommendedComingEventsResultsMap.postValue(firebaseAnswer)
        } catch(e: Exception) {
            recommendedComingEventsResultsMap.postValue(Resource.Failure(e))
        }
    }

}