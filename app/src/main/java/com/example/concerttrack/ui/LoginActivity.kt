package com.example.concerttrack.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.concerttrack.R
import com.example.concerttrack.util.Constants
import com.example.concerttrack.util.content
import com.example.concerttrack.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {


    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java) }

    private var allControls: List<View> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        allControls = listOf(text_input_email, text_input_password, logInBtn,registerBtn, forgotPasswordBtn)

        loginViewModel.userLiveData?.observe(this, Observer<FirebaseUser>{ firebaseUser ->
            if(firebaseUser != null) {
                val intent = Intent(this,FanMainPageActivity::class.java).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                }
                startActivity(intent)
            }
        })

        loginViewModel.isLoginSuccessful?.observe(this, Observer<Boolean> {
            hideSpinnerAndEnableControls()
        })





        val userTypeName = intent.getStringExtra(Constants.USER_TYPE)

        when(userTypeName) {
            Constants.ARTIST_TYPE_STR -> {
                loginInfo.text = getText(R.string.loginArtistInfo)
                userTypeIcon.setImageResource(R.drawable.star)
                registerBtn.setOnClickListener {
                    startActivity(Intent(this,ArtistRegisterActivity::class.java))
                }
            }
            Constants.FAN_TYPE_STR -> {
                loginInfo.text = getText(R.string.loginFanInfo)
                userTypeIcon.setImageResource(R.drawable.user)
                registerBtn.setOnClickListener {
                    startActivity(Intent(this,FanRegisterActivity::class.java))
                }

            }
        }

        logInBtn.setOnClickListener {
            if( areInputValid()) {
                showSpinnerAndDisableControls()
                loginViewModel.login(mailLoginET.text.toString(),passwordLoginET.text.toString())
            }
        }

        forgotPasswordBtn.setOnClickListener{
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
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