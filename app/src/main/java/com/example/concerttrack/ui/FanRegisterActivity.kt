package com.example.concerttrack.ui


import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.util.Resource
import com.example.concerttrack.util.content
import com.example.concerttrack.util.showToastError
import com.example.concerttrack.util.showToastSuccess
import com.example.concerttrack.viewmodel.FanRegisterViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_fan_register.*
import kotlinx.android.synthetic.main.activity_login.registerBtn


class FanRegisterActivity : AppCompatActivity() {

    private val fanRegisterViewModel: FanRegisterViewModel by lazy { ViewModelProvider(this).get(FanRegisterViewModel::class.java) }

    private var allControls: List<View> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fan_register)

        allControls = listOf(text_input_userName, text_input_email, text_input_password,text_input_repeat_password, registerBtn)


        fanRegisterViewModel.registerUIDLiveData.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    fanRegisterViewModel.addNewFan(it.data,userEmail.text.toString(),userName.text.toString())
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()

                    when(it.throwable.javaClass.simpleName) {
                        "FirebaseAuthUserCollisionException" -> {
                            this.showToastError(R.string.accountExists)
                        }
                        else -> {
                            this.showToastError(R.string.unknownRegisterError)
                        }
                    }
                }
            }
        })

        fanRegisterViewModel.successfullyAddedFan.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    hideSpinnerAndEnableControls()
                    this.showToastSuccess(R.string.registerSuccess)
                    finish()
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()

                    this.showToastError(R.string.unknownRegisterError)
                }
            }
        })


        registerBtn.setOnClickListener {
            if(areInputValid()){
                fanRegisterViewModel.registerUser(userEmail.text.toString(),userPassword.text.toString(), userName.text.toString())
            }
        }


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

        return when {
            isEmpty -> {
                text_input_userName.error = getString(R.string.notAllowedEmptyField)
                false
            }
            text_input_userName.editText?.length()!! > 15 -> {
                text_input_userName.error = getString(R.string.tooLongUserName)
                false
            }
            else -> {
                text_input_userName.error = null
                true
            }
        }

    }

    private fun validateEmail(): Boolean {
        val isEmpty: Boolean = text_input_email.editText?.content()?.isEmpty() ?: true

        return if(isEmpty) {
            text_input_email.error = getString(R.string.notAllowedEmptyField)
            false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(text_input_email.editText?.content()).matches()) {
            text_input_email.error = getString(R.string.badlyFormattedEmail)
            false
        } else {
            text_input_email.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val isEmpty: Boolean = text_input_password.editText?.content()?.isEmpty() ?: true

        return if(isEmpty) {
            text_input_password.error = getString(R.string.notAllowedEmptyField)
            false
        } else {
            text_input_password.error = null
            true
        }
    }

    private fun validateRepeatPassword(): Boolean {
        val isEmpty: Boolean = text_input_repeat_password.editText?.content()?.isEmpty() ?: true

        return if(isEmpty) {
            text_input_repeat_password.error = getString(R.string.notAllowedEmptyField)
            false
        } else {
            text_input_repeat_password.error = null
            true
        }
    }

    private fun validateEqualPassword(): Boolean {
        val password = text_input_password.editText?.content()
        val repeatPassword = text_input_repeat_password.editText?.content()

        return if(repeatPassword!=password) {
            text_input_password.error = getString(R.string.differentPasswordMsg)
            text_input_repeat_password.error = getString(R.string.differentPasswordMsg)
            false
        } else validatePasswordLength()
    }

    private fun validatePasswordLength(): Boolean {
        return if (text_input_password.editText?.length()!!  <6){
            text_input_password.error = getString(R.string.passwordTooShort)
            text_input_repeat_password.error = getString(R.string.passwordTooShort)
            false
        } else {
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
