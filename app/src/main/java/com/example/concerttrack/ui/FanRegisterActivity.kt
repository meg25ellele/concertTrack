package com.example.concerttrack.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.content
import com.example.concerttrack.viewmodel.FanRegisterViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_fan_register.*
import kotlinx.android.synthetic.main.activity_fan_register.text_input_email
import kotlinx.android.synthetic.main.activity_fan_register.text_input_password
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.registerBtn


class FanRegisterActivity : AppCompatActivity() {

    private val fanRegisterViewModel: FanRegisterViewModel by lazy { ViewModelProvider(this).get(FanRegisterViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fan_register)

        registerBtn.setOnClickListener {
            if(areInputValid()){
                fanRegisterViewModel.register(userEmail.text.toString(),userPassword.text.toString())
            }
        }

        fanRegisterViewModel.userLiveData?.observe(this,Observer<FirebaseUser>{ firebaseUser ->
            if(firebaseUser != null) {
                Toast.makeText(this,"User created",Toast.LENGTH_SHORT).show()
                finish()
            }
        })


    }

    private fun areInputValid(): Boolean {
        val isUserName = validateUserName()
        val isEmail = validateEmail()
        val isPassword = validatePassword()
        val isRepeatPassword = validateRepeatPassword()

        if(isUserName && isEmail && isPassword && isRepeatPassword) {
             val arePasswordsIdentical = validateEqualPassword()

            return arePasswordsIdentical
        } else   return false


    }


    private fun validateUserName(): Boolean {
        val isEmpty: Boolean = text_input_userName.editText?.content()?.isEmpty() ?: true

        if(isEmpty) {
            text_input_userName.error = getString(R.string.notAllowedEmptyField)
            return false
        } else if(text_input_userName.editText?.length()!! > 15 ) {
            text_input_userName.error = getString(R.string.tooLongUserName)
            return false
        } else {
            text_input_userName.error = null
            return true
        }

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

    private fun validateRepeatPassword(): Boolean {
        val isEmpty: Boolean = text_input_repeat_password.editText?.content()?.isEmpty() ?: true

        if(isEmpty) {
            text_input_repeat_password.error = getString(R.string.notAllowedEmptyField)
            return false
        }
        else {
                text_input_repeat_password.error = null
                return true
            }
        }

    private fun validateEqualPassword(): Boolean {
        val password = text_input_password.editText?.content()
        val repeatPassword = text_input_repeat_password.editText?.content()

        if(repeatPassword!=password) {
            text_input_password.error = getString(R.string.differentPasswordMsg)
            text_input_repeat_password.error = getString(R.string.differentPasswordMsg)
            return false
        }
        else return validatePasswordLength()
    }

    private fun validatePasswordLength(): Boolean {
        if (text_input_password.editText?.length()!!  <6){
            text_input_password.error = getString(R.string.passwordTooShort)
            text_input_repeat_password.error = getString(R.string.passwordTooShort)
            return false
        } else {
            return true
        }
    }



}
