package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.concerttrack.repository.AuthAppRepository

class SplashScreenViewModel(application: Application) : AndroidViewModel(application) {

    val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    var isLoginLiveData: MutableLiveData<Boolean>? = null

    init {
        isLoginLiveData = authAppRepository.isUserLogin
    }

}