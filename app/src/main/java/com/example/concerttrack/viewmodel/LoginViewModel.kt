package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }

    val successfullyLoginLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()



    fun loginUser(email: String, password: String)  = viewModelScope.launch {
        successfullyLoginLiveData.postValue(Resource.Loading())
        try {
            val answer = authAppRepository.loginUser(email,password)
            successfullyLoginLiveData.postValue(answer)

        } catch(e:Exception) {
            successfullyLoginLiveData.postValue(Resource.Failure(e))
        }
    }


}