package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.util.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.lang.Exception

class FanRegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }

    val successfullyRegisterLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    fun registerUser(email:String, password: String, name: String) = viewModelScope.launch {
        successfullyRegisterLiveData.postValue(Resource.Loading())
        try {
            val answer = authAppRepository.registerUser(email,password,name)
            successfullyRegisterLiveData.postValue(answer)
        } catch (e: Exception) {
            successfullyRegisterLiveData.postValue(Resource.Failure(e))
        }

    }

}



