package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.repository.CloudFirestoreRepository
import com.example.concerttrack.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class ForgotPasswordViewModel(application: Application) :AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    private  val cloudFirestoreRepository: CloudFirestoreRepository by lazy { CloudFirestoreRepository(application) }


    val isResetEmailSendLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val artistFound: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val fanFound: MutableLiveData<Resource<Boolean>> = MutableLiveData()


    fun sendResetPasswordEmail(email: String) = viewModelScope.launch {
        isResetEmailSendLiveData.postValue(Resource.Loading())
        try {
            val answer = authAppRepository.sendResetPasswordEmail(email)
            isResetEmailSendLiveData.postValue(answer)
        } catch (e:Exception) {
            isResetEmailSendLiveData.postValue(Resource.Failure(e))
        }
    }
    fun findArtist(email: String)  = viewModelScope.launch {
        artistFound. postValue(Resource.Loading())
        try{
            val firebaseAnswer = cloudFirestoreRepository.findArtist(email)
            artistFound.postValue(firebaseAnswer)
        } catch (e:Exception){
            artistFound.postValue(Resource.Failure(e))
        }
    }

    fun findFan(email: String)  = viewModelScope.launch {
        fanFound. postValue(Resource.Loading())
        try{
            val firebaseAnswer = cloudFirestoreRepository.findFan(email)
            fanFound.postValue(firebaseAnswer)
        } catch (e:Exception){
            fanFound.postValue(Resource.Failure(e))
        }
    }



}