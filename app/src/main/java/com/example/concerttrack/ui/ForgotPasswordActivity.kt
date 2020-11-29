package com.example.concerttrack.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.util.*
import com.example.concerttrack.viewmodel.ForgotPasswordViewModel
import com.example.concerttrack.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_forgot_password.mailLoginET
import kotlinx.android.synthetic.main.activity_forgot_password.progressBar
import kotlinx.android.synthetic.main.activity_forgot_password.text_input_email
import kotlinx.android.synthetic.main.activity_login.*

class ForgotPasswordActivity : AppCompatActivity() {

    private val forgotPasswordViewModel:ForgotPasswordViewModel by lazy {
        ViewModelProvider(this).get(ForgotPasswordViewModel::class.java) }

    private var allControls: List<View> = listOf()
    private var userType =  Constants.FAN_TYPE_STR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        allControls = listOf(text_input_email, sendEmailBt)

        userType = intent.getStringExtra(Constants.USER_TYPE)!!

        forgotPasswordViewModel.artistFound.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    if(it.data) {
                        forgotPasswordViewModel.sendResetPasswordEmail(mailLoginET.text.toString())
                    } else {
                        hideSpinnerAndEnableControls()
                        this.showToastError(R.string.wrongEmailErrorArtist)
                    }
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(this,it.throwable.toString(), Toast.LENGTH_LONG).show()
                }
            }
        })

        forgotPasswordViewModel.fanFound.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    if(it.data) {
                        forgotPasswordViewModel.sendResetPasswordEmail(mailLoginET.text.toString())
                    } else {
                        hideSpinnerAndEnableControls()
                        this.showToastError(R.string.wrongEmailErrorFan)
                    }
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    Toast.makeText(this,it.throwable.toString(), Toast.LENGTH_LONG).show()
                }
            }
        })


        forgotPasswordViewModel.isResetEmailSendLiveData.observe(this, Observer {
            when(it) {

                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    hideSpinnerAndEnableControls()
                    this.showToastSuccess(R.string.sendEmailSuccess)
                    finish()
                }

                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()

                    when(it.throwable.javaClass.simpleName) {
                        "FirebaseAuthInvalidUserException" -> {
                            this.showToastError(R.string.wrongEmailError)
                        }
                        else -> {
                            this.showToastError(R.string.sendEmailError)
                        }
                    }

                }
            }
        })


        sendEmailBt.setOnClickListener {
            if(isEmailCorrect()){

                when (userType) {
                    Constants.ARTIST_TYPE_STR -> {
                        forgotPasswordViewModel.findArtist(mailLoginET.text.toString())
                    }
                    Constants.FAN_TYPE_STR -> {
                        forgotPasswordViewModel.findFan(mailLoginET.text.toString())
                    }
                }
            }
        }
    }

    private fun isEmailCorrect():Boolean {
        val emailEditText = text_input_email.editText?.content().toString()

        return if(FormValidators.isInputEmpty(emailEditText)) {
            text_input_email.error = getString(R.string.notAllowedEmptyField)
            false
        }else if(!FormValidators.isEmailCorrect(emailEditText)) {
            text_input_email.error = getString(R.string.badlyFormattedEmail)
            false
        } else {
            text_input_email.error = null
            true
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


}