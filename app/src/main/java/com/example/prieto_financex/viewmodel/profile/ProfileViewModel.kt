package com.example.prieto_financex.viewmodel.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.prieto_financex.data.locals.AppDatabaseHelper
import com.example.prieto_financex.data.model.UserData
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = AppDatabaseHelper(application.applicationContext)
    private val auth = FirebaseAuth.getInstance()

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> = _userData

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Load user data based on email
    fun loadUserData(email: String) {
        _userData.value = dbHelper.getUserByEmail(email)
    }

    // Update user profile with the provided information
    fun updateUserProfile(
        updatedUsername: String,
        updatedEmail: String,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String,
        selectedImageUri: Uri?,
        getString: (Int) -> String
    ) {
        val currentUser = dbHelper.getUserByEmail(updatedEmail)
        if (currentUser == null) {
            _errorMessage.value = getString(com.example.prieto_financex.R.string.user_not_found)
            return
        }

        if (newPassword.isNotEmpty() && newPassword != confirmPassword) {
            _errorMessage.value = getString(com.example.prieto_financex.R.string.passwords_do_not_match)
            return
        }

        val passwordToUse = when {
            newPassword.isNotEmpty() -> newPassword
            oldPassword.isNotEmpty() -> oldPassword
            else -> currentUser.userPassword
        }

        val updatedUser = UserData(
            userId = currentUser.userId,
            userName = updatedUsername,
            userEmail = updatedEmail,
            userPassword = passwordToUse,
            isVerified = currentUser.isVerified,
            profileImageUrl = selectedImageUri?.toString() ?: currentUser.profileImageUrl
        )

        // Update the user in the local database
        val rowsUpdated = dbHelper.updateUser(updatedUser)
        if (rowsUpdated > 0) {
            // If password is changed, update Firebase authentication password
            if (newPassword.isNotEmpty()) {
                auth.currentUser?.updatePassword(newPassword)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _updateSuccess.value = true
                        } else {
                            _errorMessage.value = getString(com.example.prieto_financex.R.string.password_update_failed)
                        }
                    }
            } else {
                _updateSuccess.value = true
            }
        } else {
            _errorMessage.value = getString(com.example.prieto_financex.R.string.update_failed)
        }
    }
}