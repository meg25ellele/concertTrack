package com.example.concerttrack.ui


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.util.*
import com.example.concerttrack.viewmodel.FanRegisterViewModel
import kotlinx.android.synthetic.main.activity_fan_register.*
import kotlinx.android.synthetic.main.activity_fan_register.progressBar
import kotlinx.android.synthetic.main.activity_fan_register.repeatPassword
import kotlinx.android.synthetic.main.activity_fan_register.text_input_email
import kotlinx.android.synthetic.main.activity_fan_register.text_input_password
import kotlinx.android.synthetic.main.activity_fan_register.text_input_repeat_password
import kotlinx.android.synthetic.main.activity_fan_register.text_input_userName
import kotlinx.android.synthetic.main.activity_fan_register.userEmail
import kotlinx.android.synthetic.main.activity_fan_register.userName
import kotlinx.android.synthetic.main.activity_fan_register.userPassword
import kotlinx.android.synthetic.main.activity_login.registerBtn
import kotlinx.android.synthetic.main.fragment_artist_register_first.*


class FanRegisterActivity : AppCompatActivity() {

    private val fanRegisterViewModel: FanRegisterViewModel by lazy { ViewModelProvider(this).get(FanRegisterViewModel::class.java) }

    private var allControls: List<View> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fan_register)

        allControls = listOf(text_input_userName, text_input_email, text_input_password,text_input_repeat_password, registerBtn)

        userName.addTextChangedListener {
            text_input_userName.error = null
        }

        userEmail.addTextChangedListener {
            text_input_email.error = null
        }

        userPassword.addTextChangedListener {
            text_input_password.error = null
        }

        repeatPassword.addTextChangedListener {
            text_input_repeat_password.error = null
        }

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
        val nameEditText = text_input_userName.editText?.content().toString()

        return when {
            FormValidators.isInputEmpty(nameEditText) -> {
                text_input_userName.error = getString(R.string.notAllowedEmptyField)
                false
            }
            FormValidators.isFanNameTooLong(nameEditText) -> {
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

    private fun validatePassword(): Boolean {
        val passwordEditText = text_input_password.editText?.content().toString()

        return if(FormValidators.isInputEmpty(passwordEditText)) {
            text_input_password.error = getString(R.string.notAllowedEmptyField)
            false
        } else {
            text_input_password.error = null
            true
        }
    }

    private fun validateRepeatPassword(): Boolean {
        val repeatPasswordEditText = text_input_repeat_password.editText?.content().toString()

        return if(FormValidators.isInputEmpty(repeatPasswordEditText)) {
            text_input_repeat_password.error = getString(R.string.notAllowedEmptyField)
            false
        } else {
            text_input_repeat_password.error = null
            true
        }
    }

    private fun validateEqualPassword(): Boolean {
        val password = text_input_password.editText?.content().toString()
        val repeatPassword = text_input_repeat_password.editText?.content().toString()

        return if(!FormValidators.arePasswordsEquals(password,repeatPassword)) {
            text_input_password.error = getString(R.string.differentPasswordMsg)
            text_input_repeat_password.error = getString(R.string.differentPasswordMsg)
            false
        } else validatePasswordLength()
    }

    private fun validatePasswordLength(): Boolean {
        val password = text_input_password.editText?.content().toString()

        return if (FormValidators.isPasswordTooShort(password)){
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
