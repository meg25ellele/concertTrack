package com.example.concerttrack.ui.artist_register_fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.concerttrack.R
import com.example.concerttrack.util.Resource
import com.example.concerttrack.util.content
import com.example.concerttrack.util.showToastError
import com.example.concerttrack.util.showToastSuccess
import com.example.concerttrack.viewmodel.ArtistRegisterViewModel
import com.google.firebase.auth.FirebaseUser
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allControls = listOf(text_input_userName, text_input_email, text_input_password,text_input_repeat_password, registerBtn, nextFragmentBtn)


        nextFragmentBtn.setOnClickListener {view ->
//            if(areInputValid()){
//                view.findNavController().navigate(R.id.action_artistRegisterFirstFragment_to_artistRegisterSecondFragment)
//            }

            view.findNavController().navigate(R.id.action_artistRegisterFirstFragment_to_artistRegisterSecondFragment)
        }

        artistRegisterViewModel.successfullyRegisterLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> {
                    showSpinnerAndDisableControls()
                }
                is Resource.Success -> {
                    hideSpinnerAndEnableControls()
                    this.showToastSuccess(R.string.registerSuccess)
                    activity?.finish()
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


        registerBtn.setOnClickListener {
            if(areInputValid()){
                artistRegisterViewModel.registerUser(userEmail.text.toString(),userPassword.text.toString(), userName.text.toString())
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