package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.models.Fan
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class FanRegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    private val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }

    val registerUIDLiveData: MutableLiveData<Resource<String>> = MutableLiveData()
    val successfullyAddedFan: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    fun registerUser(email:String, password: String, name: String) = viewModelScope.launch {
        registerUIDLiveData.postValue(Resource.Loading())
        try {
            val registerAnswer = authAppRepository.registerUser(email,password)
            registerUIDLiveData.postValue(registerAnswer)
        } catch (e: Exception) {
            registerUIDLiveData.postValue(Resource.Failure(e))
        }
    }

    fun addNewFan(id: String, email: String, name: String) = viewModelScope.launch {
        successfullyAddedFan.postValue(Resource.Loading())
        try{
            val newUser = Fan(id,email,name)
            val firestoreAnswer = cloudFirestoreRepository.addNewFan(newUser)
            successfullyAddedFan.postValue(firestoreAnswer)
        } catch (e:Exception) {
            successfullyAddedFan.postValue(Resource.Failure(e))
        }
    }

}



