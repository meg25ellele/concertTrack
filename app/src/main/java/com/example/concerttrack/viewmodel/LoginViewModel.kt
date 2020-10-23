package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.repository.AuthAppRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    var userLiveData: MutableLiveData<FirebaseUser>? = null
    var isLoginSuccessful: MutableLiveData<Boolean>? = null

    init {
        userLiveData = authAppRepository.userLiveData
        isLoginSuccessful = authAppRepository.isLoginSuccessful
    }

    fun login(email: String, password: String){
            authAppRepository.loginUser(email,password)
    }
}