package com.example.prieto_financex.viewmodel.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.prieto_financex.R
import com.example.prieto_financex.data.locals.AppDatabaseHelper
import com.example.prieto_financex.data.model.UserData
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = Firebase.auth // Use KTX version
    private val dbHelper = AppDatabaseHelper(application)

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun registerUser(username: String, email: String, password: String) {
        // Validate inputs
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _errorMessage.value = getApplication<Application>().getString(R.string.fill_both_fields)
            return
        }

        _isLoading.value = true

        // Check Firebase initialization
        if (FirebaseApp.getApps(getApplication()).isEmpty()) {
            _errorMessage.value = "Firebase not initialized"
            _isLoading.value = false
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    handleSuccessfulRegistration(username, email)
                } else {
                    handleRegistrationFailure(task.exception)
                }
                _isLoading.value = false
            }
    }

    private fun handleSuccessfulRegistration(username: String, email: String) {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { verifyTask ->
                if (verifyTask.isSuccessful) {
                    saveUserToLocalDb(username, email)
                } else {
                    _errorMessage.value = getApplication<Application>()
                        .getString(R.string.verification_email_failed)
                }
            }
    }

    private fun saveUserToLocalDb(username: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userData = UserData(
                    userId = 0,
                    userName = username,
                    userEmail = email,
                    userPassword = "", // Don't store password in local DB
                    isVerified = false
                )

                val rowId = dbHelper.insertUser(userData)
                if (rowId != -1L) {
                    _registrationSuccess.postValue(true)
                } else {
                    _errorMessage.postValue(
                        getApplication<Application>().getString(R.string.sqlite_save_failed)
                    )
                }
            } catch (e: Exception) {
                _errorMessage.postValue(
                    getApplication<Application>().getString(R.string.database_error)
                )
            }
        }
    }

    private fun handleRegistrationFailure(exception: Exception?) {
        val errorMsg = when (exception) {
            is FirebaseAuthUserCollisionException ->
                getApplication<Application>().getString(R.string.email_already_used)
            is FirebaseAuthWeakPasswordException ->
                getApplication<Application>().getString(R.string.weak_password)
            is FirebaseAuthInvalidCredentialsException ->
                getApplication<Application>().getString(R.string.invalid_email)
            else ->
                getApplication<Application>()
                    .getString(R.string.registration_failed, exception?.message ?: "Unknown error")
        }
        _errorMessage.value = errorMsg
    }
}