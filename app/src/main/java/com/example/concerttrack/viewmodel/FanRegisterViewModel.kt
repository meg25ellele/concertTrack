package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.util.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.lang.Exception

class FanRegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    private val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }

    val registerUIDLiveData: MutableLiveData<Resource<String>> = MutableLiveData()
    val successfullyAddedUser: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    fun registerUser(email:String, password: String, name: String) = viewModelScope.launch {
        registerUIDLiveData.postValue(Resource.Loading())
        try {
            val registerAnswer = authAppRepository.registerUser(email,password)
            registerUIDLiveData.postValue(registerAnswer)
        } catch (e: Exception) {
            registerUIDLiveData.postValue(Resource.Failure(e))
        }
    }

    fun addNewUser(id: String, email: String, name: String) = viewModelScope.launch {
        successfullyAddedUser.postValue(Resource.Loading())
        try{
            val firestoreAnswer = cloudFirestoreRepository.addNewUser(id,email,name)
            successfullyAddedUser.postValue(firestoreAnswer)
        } catch (e:Exception) {
            successfullyAddedUser.postValue(Resource.Failure(e))
        }
    }

}



