package com.example.concerttrack.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.util.content
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        allControls = listOf(text_input_email, sendEmailBt)

        forgotPasswordViewModel.isResetEmailSend?.observe(this, Observer<Boolean> {isResetEmailSend ->
            hideSpinnerAndEnableControls()
            if(isResetEmailSend){
                finish()
            }
        })


        sendEmailBt.setOnClickListener {
            if(isEmailCorrect()){
                showSpinnerAndDisableControls()
                forgotPasswordViewModel.sendEmail(mailLoginET.text.toString())
            }
        }
    }

    private fun isEmailCorrect():Boolean {
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

    private fun showSpinnerAndDisableControls() {
        progressBar.visibility = View.VISIBLE
        allControls.forEach { v -> v.isEnabled = false }
    }

    private  fun hideSpinnerAndEnableControls() {
        progressBar.visibility = View.GONE
        allControls.forEach { v -> v.isEnabled = true }
    }


}