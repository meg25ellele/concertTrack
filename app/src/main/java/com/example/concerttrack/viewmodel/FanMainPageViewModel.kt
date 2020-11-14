package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.models.Artist
import com.example.concerttrack.models.Fan
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.repository.StorageRepository
import com.example.concerttrack.util.Resource
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.launch
import java.lang.Exception

class FanMainPageViewModel(application: Application) : AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }

    val isFanLoggedOut: MutableLiveData<Boolean> = MutableLiveData()
    var fanLiveData: MutableLiveData<Resource<Fan>> = MutableLiveData()

    fun logOut(){
        val answer = authAppRepository.logOut()
        isFanLoggedOut.postValue(answer.data)
    }

    fun getCurrentUser(): FirebaseUser {
        return authAppRepository.getCurrentUser().data
    }

    fun getFanData(email: String) = viewModelScope.launch {
        fanLiveData.postValue(Resource.Loading())
        try {
            val firebaseAnswer = cloudFirestoreRepository.getFan(email)
            fanLiveData.postValue(firebaseAnswer)
        } catch(e: Exception) {
            fanLiveData.postValue(Resource.Failure(e))
        }
    }
}