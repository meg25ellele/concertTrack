package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.concerttrack.models.Artist
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.repository.StorageRepository
import com.example.concerttrack.util.Resource
import com.google.firebase.auth.FirebaseUser

class FanMainPageViewModel(application: Application) : AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }

    val isUserLoggedOut: MutableLiveData<Boolean> = MutableLiveData()

    fun logOut(){
        val answer = authAppRepository.logOut()
        isUserLoggedOut.postValue(answer.data)
    }








}