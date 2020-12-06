package com.example.concerttrack.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.concerttrack.R
import com.example.concerttrack.util.*
import com.example.concerttrack.viewmodel.ArtistRegisterViewModel
import kotlinx.android.synthetic.main.fragment_artist_register_first.*
import kotlinx.android.synthetic.main.fragment_artist_register_first.progressBar
import kotlinx.android.synthetic.main.fragment_artist_register_first.registerBtn
import kotlinx.android.synthetic.main.fragment_artist_register_first.text_input_email
import kotlinx.android.synthetic.main.fragment_artist_register_first.text_input_password
import kotlinx.android.synthetic.main.fragment_artist_register_first.text_input_repeat_password
import kotlinx.android.synthetic.main.fragment_artist_register_first.text_input_userName

class ArtistRegisterFirstFragment: Fragment(R.layout.fragment_artist_register_first) {

    private val artistRegisterViewModel: ArtistRegisterViewModel by lazy { ViewModelProvider(this).get(
        ArtistRegisterViewModel::class.java) }

    private var allControls: List<View> = listOf()
    private var registrationOn: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allControls = listOf(text_input_userName, text_input_email, text_input_password,text_input_repeat_password, registerBtn, nextFragmentBtn)


        nextFragmentBtn.setOnClickListener {view ->
            if(areInputValid()){
                artistRegisterViewModel.alreadyExists(userName.text.toString())
            }
        }

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

        artistRegisterViewModel.isNameTaken.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    if(!it.data) {
                        if(registrationOn) {
                            artistRegisterViewModel.registerUser(userEmail.text.toString(),userPassword.text.toString(), userName.text.toString())
                        }
                        else {
                            val bundle = bundleOf(getString(R.string.name) to userName.text.toString(),
                                getString(R.string.email) to userEmail.text.toString(),
                                getString(R.string.password) to userPassword.text.toString())
                            view.findNavController().navigate(R.id.action_artistRegisterFirstFragment_to_artistRegisterSecondFragment,bundle)
                        }
                    } else {
                        hideSpinnerAndEnableControls()
                        text_input_userName.error = getString(R.string.duplicatedArtistName)
                    }
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    this.showToastError(R.string.unknownRegisterError)
                }
            }
        })

        artistRegisterViewModel.registerUIDLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    artistRegisterViewModel.addNewArtist(it.data,userEmail.text.toString().toLowerCase(),userName.text.toString(),
                        "","","","",null)
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

        artistRegisterViewModel.successfullyAddedArtist.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    hideSpinnerAndEnableControls()
                    this.showToastSuccess(R.string.registerSuccess)
                    activity?.finish()
                }
                is Resource.Failure -> {
                    hideSpinnerAndEnableControls()
                    this.showToastError(R.string.unknownRegisterError)
                }
            }
        })


        registerBtn.setOnClickListener {
            if(areInputValid()){
                registrationOn = true
                artistRegisterViewModel.alreadyExists(userName.text.toString())
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
            else -> {
                text_input_userName.error = null
                true
            }
        }
    }

    private fun validateEmail(): Boolean {
        val emailTextView = text_input_email.editText?.content().toString()

        return if(FormValidators.isInputEmpty(emailTextView)) {
            text_input_email.error = getString(R.string.notAllowedEmptyField)
            false
        }else if(!FormValidators.isEmailCorrect(emailTextView)) {
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
        val passwordEditText = text_input_password.editText?.content().toString()

        return if (FormValidators.isPasswordTooShort(passwordEditText)){
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