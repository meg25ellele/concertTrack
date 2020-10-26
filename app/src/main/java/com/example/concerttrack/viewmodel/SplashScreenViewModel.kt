package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.concerttrack.repository.AuthAppRepository

class SplashScreenViewModel(application: Application) : AndroidViewModel(application) {

    val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    var isUserLoginLiveData: MutableLiveData<Boolean> = MutableLiveData()


    fun checkUserLogin(){
        val user = authAppRepository.checkUserLogin()
        isUserLoginLiveData.postValue(user.data)
    }

}