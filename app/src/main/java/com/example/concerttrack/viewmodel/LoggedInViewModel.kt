package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.concerttrack.repository.AuthAppRepository
import com.google.firebase.auth.FirebaseUser

class LoggedInViewModel(application: Application) : AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    var userLiveData: MutableLiveData<FirebaseUser>? = null
    var isUserLogin: MutableLiveData<Boolean>? = null

    init{
        userLiveData = authAppRepository.userLiveData
        isUserLogin = authAppRepository.isUserLogin
    }

    fun logOut(){
        authAppRepository.logOut()
    }
}