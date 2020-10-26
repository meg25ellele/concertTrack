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

    var userLiveData: MutableLiveData<FirebaseUser>? = null
    var isUserLogin: MutableLiveData<Boolean>? = null
    var isLoginSuccessful: MutableLiveData<Boolean>? = null
    var isRegisterSuccessful: MutableLiveData<Boolean>? = null
    var isResetEmailSend: MutableLiveData<Boolean>? = null

    private var firebaseAuth: FirebaseAuth

    init {
        userLiveData = MutableLiveData<FirebaseUser>()
        isUserLogin = MutableLiveData<Boolean>()
        isLoginSuccessful = MutableLiveData<Boolean>()
        isRegisterSuccessful = MutableLiveData<Boolean>()
        isResetEmailSend = MutableLiveData<Boolean>()

        firebaseAuth = FirebaseAuth.getInstance()

        if(firebaseAuth.currentUser != null) {
            userLiveData!!.postValue(firebaseAuth.currentUser)
            isUserLogin!!.postValue(true)
        } else {
            isUserLogin!!.postValue(false)
        }



    }


    suspend fun registerUser(email:String, password: String, name: String) : Resource<FirebaseUser?> {
        firebaseAuth.createUserWithEmailAndPassword(email,password).await()

        firebaseAuth.currentUser?.let { user ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            user.updateProfile(profileUpdates).await()
        }

        return Resource.Success(firebaseAuth.currentUser)
    }



    fun registerUser1(email: String, password: String, name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email,password).await()
                withContext(Dispatchers.Main) {

                    firebaseAuth.currentUser?.let {user->
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                user.updateProfile(profileUpdates).await()
                            } catch(e: Exception){
                                withContext(Dispatchers.Main){
                                    Toast.makeText(application,e.message,Toast.LENGTH_LONG).show()
                                }

                            }

                        }
                    }

                    userLiveData?.postValue(firebaseAuth.currentUser)
                    isRegisterSuccessful?.postValue(true)
                    logOut()
                    application.showToastSuccess(R.string.registerSuccess)
                }
            }
            catch (e: FirebaseAuthUserCollisionException) {
                withContext(Dispatchers.Main){
                    isRegisterSuccessful?.postValue(false)
                    application.showToastError(R.string.accountExists)
                }
            }
            catch (e: Exception) {
                withContext(Dispatchers.Main){
                    isRegisterSuccessful?.postValue(false)
                    application.showToastError(R.string.unknownRegisterError)                }
            }
        }
    }

    fun loginUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firebaseAuth.signInWithEmailAndPassword(email,password).await()
                withContext(Dispatchers.Main) {
                    userLiveData?.postValue(firebaseAuth.currentUser)
                    isUserLogin?.postValue(true)
                    isLoginSuccessful?.postValue(true)
                    application.showToastSuccess(R.string.loginSuccess)
                }
            }
            catch(e:FirebaseAuthInvalidUserException) {
                withContext(Dispatchers.Main){
                    isLoginSuccessful?.postValue(false)
                    application.showToastError(R.string.wrongPassword)
                }
            }
            catch(e:FirebaseAuthInvalidCredentialsException){
                withContext(Dispatchers.Main){
                    isLoginSuccessful?.postValue(false)
                    application.showToastError(R.string.noSuchAccount)
                }
            }
            catch (e: Exception) {
                withContext(Dispatchers.Main){
                    isLoginSuccessful?.postValue(false)
                    application.showToastError(R.string.unknownLoginError)
                }
            }
        }
    }

    fun sendEmail(email: String){
        CoroutineScope(Dispatchers.IO).launch {
            try{
                firebaseAuth.sendPasswordResetEmail(email).await()
                withContext(Dispatchers.Main) {
                    isResetEmailSend?.postValue(true)
                    application.showToastSuccess(R.string.sendEmailSuccess)

                }

            }catch (e: FirebaseAuthInvalidUserException){
                withContext(Dispatchers.Main){
                    isResetEmailSend?.postValue(false)
                    application.showToastError(R.string.wrongEmailError)
                }
            }
            catch(e: Exception){
                withContext(Dispatchers.Main){
                    isResetEmailSend?.postValue(false)
                    application.showToastError(R.string.unknownLoginError)
                }

            }
        }
    }


    fun logOut(){
        firebaseAuth.signOut()
        isUserLogin?.postValue(false)
    }



}