package com.example.prieto_financex.viewmodel.auth

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.prieto_financex.R
import com.example.prieto_financex.data.locals.AppDatabaseHelper
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val dbHelper = AppDatabaseHelper(application.applicationContext)
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Modified login function with your custom branding (blue theme logic)
    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage.value = context.getString(R.string.email_password_required)
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Custom update logic based on your app's requirements
                        val updated = dbHelper.updateVerificationStatus(email, true)
                        if (updated <= 0) {
                            _errorMessage.value = context.getString(R.string.update_verification_failed)
                        }
                        _loginSuccess.value = true
                    } else {
                        _errorMessage.value = context.getString(R.string.please_verify_email)
                        auth.signOut()
                    }
                } else {
                    // Display a customized error message in case of login failure
                    _errorMessage.value = context.getString(
                        R.string.login_failed,
                        task.exception?.message ?: context.getString(R.string.error_unknown)
                    )
                }
            }
    }

    // Checks if the user is already logged in
    fun isUserAlreadyLoggedIn(): Boolean {
        val user = auth.currentUser
        return user != null && user.isEmailVerified
    }
}