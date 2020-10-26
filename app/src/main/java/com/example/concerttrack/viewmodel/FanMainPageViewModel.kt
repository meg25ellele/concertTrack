package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.util.Resource
import com.google.firebase.auth.FirebaseUser

class FanMainPageViewModel(application: Application) : AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }

    val userLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    val isUserLoggedOut: MutableLiveData<Boolean> = MutableLiveData()


    fun logOut(){
        val answer = authAppRepository.logOut()
        isUserLoggedOut.postValue(answer.data)
    }

    fun getCurrentUser(): FirebaseUser {
       return authAppRepository.getCurrentUser().data
    }
}