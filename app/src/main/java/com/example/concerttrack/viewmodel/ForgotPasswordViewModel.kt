package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.concerttrack.repository.AuthAppRepository
import com.example.concerttrack.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class ForgotPasswordViewModel(application: Application) :AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }

    val isResetEmailSendLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()


    fun sendResetPasswordEmail(email: String) = viewModelScope.launch {
        isResetEmailSendLiveData.postValue(Resource.Loading())
        try {
            val answer = authAppRepository.sendResetPasswordEmail(email)
            isResetEmailSendLiveData.postValue(answer)
        } catch (e:Exception) {
            isResetEmailSendLiveData.postValue(Resource.Failure(e))
        }
    }

}