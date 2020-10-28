package com.example.concerttrack.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.util.*
import com.example.concerttrack.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java) }

    private var allControls: List<View> = listOf()
    private var userType =  Constants.FAN_TYPE_STR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        allControls = listOf(text_input_email, text_input_password, logInBtn,registerBtn, forgotPasswordBtn)


        when(intent.getStringExtra(Constants.USER_TYPE)) {
            Constants.ARTIST_TYPE_STR -> {
                userType = Constants.ARTIST_TYPE_STR
                loginInfo.text = getText(R.string.loginArtistInfo)
                userTypeIcon.setImageResource(R.drawable.star)
                registerBtn.setOnClickListener {
                    startActivity(Intent(this,ArtistRegisterActivity::class.java))
                }
            }
            Constants.FAN_TYPE_STR -> {
                userType = Constants.FAN_TYPE_STR
                loginInfo.text = getText(R.string.loginFanInfo)
                userTypeIcon.setImageResource(R.drawable.user)
                registerBtn.setOnClickListener {
                    startActivity(Intent(this,FanRegisterActivity::class.java))
                }

            }
        }

        loginViewModel.artistFound.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    if(it.data) {
                        loginViewModel.loginUser(mailLoginET.text.toString(),passwordLoginET.text.toString())
                    } else {
                        hideSpinnerAndEnableControls()
                        this.showToastError(R.string.noSuchAccountArtist)
                    }
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(this,it.throwable.toString(),Toast.LENGTH_LONG).show()
            }
            }
        })

        loginViewModel.userFound.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    if(it.data) {
                        loginViewModel.loginUser(mailLoginET.text.toString(),passwordLoginET.text.toString())
                    } else {
                        hideSpinnerAndEnableControls()
                        this.showToastError(R.string.noSuchAccountFan)
                    }
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(this,it.throwable.toString(),Toast.LENGTH_LONG).show()
                }
            }
        })


        loginViewModel.successfullyLoginLiveData.observe(this, Observer { it ->
            when(it) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {

                    when(userType) {
                        Constants.FAN_TYPE_STR -> {
                            val intent = Intent(this,FanMainPageActivity::class.java).apply {
                                addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                )
                            }
                            hideSpinnerAndEnableControls()
                            this.showToastSuccess(R.string.loginSuccess)
                            startActivity(intent)
                        }
                        Constants.ARTIST_TYPE_STR -> {
                            val intent = Intent(this,ArtistMainPage::class.java).apply {
                                addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                )
                            }
                            hideSpinnerAndEnableControls()
                            this.showToastSuccess(R.string.loginSuccess)
                            startActivity(intent)
                        }
                    }

                }

                is Resource.Failure-> {
                    hideSpinnerAndEnableControls()

                   when(it.throwable.javaClass.simpleName) {
                       "FirebaseAuthInvalidCredentialsException" -> {
                           this.showToastError(R.string.wrongPassword)
                       }
                        "FirebaseAuthInvalidUserException" -> {
                            this.showToastError(R.string.noSuchAccount)
                        }
                        else -> {
                            this.showToastError(R.string.unknownLoginError)
                        }
                    }
                }
            }
        })


        logInBtn.setOnClickListener {
            if( areInputValid()) {

                when (userType) {
                    Constants.ARTIST_TYPE_STR -> {
                        loginViewModel.findArtist(mailLoginET.text.toString())
                    }
                    Constants.FAN_TYPE_STR -> {
                        loginViewModel.findUser(mailLoginET.text.toString())
                    }
                }
            }
        }

        forgotPasswordBtn.setOnClickListener{
            val intent = Intent(this,ForgotPasswordActivity::class.java).apply {
                putExtra(Constants.USER_TYPE,userType)
            }
            startActivity(intent)
        }
    }

    private fun areInputValid(): Boolean {
        val isPassword = validatePassword()
        val isEmail = validateEmail()

        return isPassword && isEmail

    }

    private fun validateEmail(): Boolean {
       val isEmpty: Boolean = text_input_email.editText?.content()?.isEmpty() ?: true

        if(isEmpty) {
            text_input_email.error = getString(R.string.notAllowedEmptyField)
            return false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(text_input_email.editText?.content()).matches()) {
            text_input_email.error = getString(R.string.badlyFormattedEmail)
            return false
        }
        else {
            text_input_email.error = null
            return true
        }
    }

    private fun validatePassword(): Boolean {
        val isEmpty: Boolean = text_input_password.editText?.content()?.isEmpty() ?: true

        if(isEmpty) {
            text_input_password.error = getString(R.string.notAllowedEmptyField)
            return false
        }
        else {
            text_input_password.error = null
            return true
        }
    }

    private fun showSpinnerAndDisableControls() {
        progressBar.visibility = View.VISIBLE
        allControls.forEach { v -> v.isEnabled = false }
    }

    private  fun hideSpinnerAndEnableControls() {
        progressBar.visibility = View.GONE
        allControls.forEach { v -> v.isEnabled = true }
    }

    override fun onRestart() {
        super.onRestart()
        mailLoginET.text = null
        passwordLoginET.text = null
        mailLoginET.clearFocus()
        passwordLoginET.clearFocus()
        text_input_email.error = null
        text_input_password.error = null
    }



}