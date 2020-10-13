package com.example.concerttrack.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AuthAppRepository(private val application: Application) {

    var userLiveData: MutableLiveData<FirebaseUser>? = null
    var isLoginLiveData: MutableLiveData<Boolean>? = null
    var isLoginSuccessful: MutableLiveData<Boolean>? = null
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    init {
        userLiveData = MutableLiveData<FirebaseUser>()
        isLoginLiveData = MutableLiveData<Boolean>()
        isLoginSuccessful = MutableLiveData<Boolean>()

        if(firebaseAuth.currentUser != null) {
            userLiveData!!.postValue(firebaseAuth.currentUser)
            isLoginLiveData!!.postValue(true)
        } else {
            isLoginLiveData!!.postValue(false)
        }

    }

    fun registerUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                userLiveData?.postValue(firebaseAuth.currentUser)
            }
            .addOnFailureListener {
                Toast.makeText(application,"Registration failed" + it.message,Toast.LENGTH_SHORT).show()
            }

    }

    fun loginUser(email: String, password: String) {

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                userLiveData?.postValue(firebaseAuth.currentUser)
                isLoginLiveData?.postValue(true)
                isLoginSuccessful?.postValue(true)
            }
            .addOnFailureListener {
                isLoginSuccessful?.postValue(false)
                Toast.makeText(application,"Login failed" + it.message,Toast.LENGTH_SHORT).show()

            }

    }

    fun logOut(){
        firebaseAuth.signOut()
        isLoginLiveData?.postValue(false)
    }

    fun accountExists(email: String): Boolean {
        return firebaseAuth.isSignInWithEmailLink(email)
    }



}