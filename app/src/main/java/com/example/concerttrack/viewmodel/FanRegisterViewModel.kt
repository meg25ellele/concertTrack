package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.concerttrack.repository.AuthAppRepository
import com.google.firebase.auth.FirebaseUser

class FanRegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }

    var userLiveData: MutableLiveData<FirebaseUser>? = null
    var isRegisterSuccessful: MutableLiveData<Boolean>? = null




    init {
        userLiveData = authAppRepository.userLiveData
        isRegisterSuccessful = authAppRepository.isRegisterSuccessful
    }

    fun register(email: String, password: String, name: String) {
        authAppRepository.registerUser1(email,password, name)
    }


}