package com.example.concerttrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.concerttrack.repository.AuthAppRepository

class ForgotPasswordViewModel(application: Application) :AndroidViewModel(application) {

    private val authAppRepository: AuthAppRepository by lazy { AuthAppRepository(application) }
    var isResetEmailSend: MutableLiveData<Boolean>? = null


    init {
        isResetEmailSend = authAppRepository.isResetEmailSend
    }

    fun sendEmail(email: String){
        authAppRepository.sendEmail(email)
    }

}