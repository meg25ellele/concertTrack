package com.example.concerttrack.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.concerttrack.R
import com.example.concerttrack.util.Resource
import com.example.concerttrack.util.showToastError
import com.example.concerttrack.util.showToastSuccess
import com.google.firebase.auth.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception


class AuthAppRepository(private val application: Application) {


    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


    suspend fun loginUser(email:String,password: String): Resource<Boolean> {
        firebaseAuth.signInWithEmailAndPassword(email,password).await()
        return Resource.Success(true)
    }

    suspend fun registerUser(email:String,password:String): Resource<String> {
        firebaseAuth.createUserWithEmailAndPassword(email,password).await()
        val userUID = firebaseAuth.currentUser!!.uid
        logOut()
        return Resource.Success(userUID)
    }

    suspend fun sendResetPasswordEmail(email: String): Resource<Boolean> {
        firebaseAuth.sendPasswordResetEmail(email).await()
        return Resource.Success(true)
    }

    fun logOut() : Resource.Success<Boolean> {
        firebaseAuth.signOut()
        return Resource.Success(true)
    }

     fun checkUserLogin(): Resource.Success<String?> {
         return if(firebaseAuth.currentUser!= null){
             Resource.Success(firebaseAuth.currentUser!!.email)
         } else Resource.Success(null)
    }


    fun getCurrentUser() : Resource.Success<FirebaseUser> {
        return Resource.Success(firebaseAuth.currentUser!!)
    }
}