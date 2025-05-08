package com.example.prieto_financex.viewmodel.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.prieto_financex.data.locals.AppDatabaseHelper
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val dbHelper = AppDatabaseHelper(application.applicationContext)

    // LiveData for deletion success and error message
    private val _deletionSuccess = MutableLiveData<Boolean>()
    val deletionSuccess: LiveData<Boolean> = _deletionSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    /**
     * Deletes the user from both Firebase and local database
     */
    fun deleteUser(getString: (Int) -> String, getStringWithArg: (Int, String) -> String) {
        val user = auth.currentUser
        val email = user?.email ?: return

        // Fetch user data from local database using email
        val userData = dbHelper.getUserByEmail(email)
        val password = userData?.userPassword ?: run {
            _errorMessage.value = getString(getStringRes("reauthentication_failed"))
            return
        }

        // Create authentication credential with email and password
        val credential = EmailAuthProvider.getCredential(email, password)

        // Re-authenticate the user before deleting
        user.reauthenticate(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                // If re-authentication is successful, delete the user from Firebase
                user.delete().addOnCompleteListener { deleteTask ->
                    if (deleteTask.isSuccessful) {
                        // If Firebase user deletion is successful, delete from local database
                        val rows = dbHelper.deleteUserByEmail(email)
                        if (rows > 0) {
                            _deletionSuccess.value = true
                        }
                    } else {
                        // Handle error if Firebase deletion fails
                        val error = deleteTask.exception?.message ?: getString(getStringRes("error_unknown"))
                        _errorMessage.value = getStringWithArg(getStringRes("error_unknown"), error)
                    }
                }
            } else {
                // Handle re-authentication failure
                val error = authTask.exception?.message ?: getString(getStringRes("error_unknown"))
                _errorMessage.value = getStringWithArg(getStringRes("reauthentication_failed"), error)
            }
        }
    }

    /**
     * Helper function to get string resource IDs consistently.
     */
    private fun getStringRes(name: String): Int {
        return when (name) {
            "reauthentication_failed" -> com.example.prieto_financex.R.string.reauthentication_failed
            "error_unknown" -> com.example.prieto_financex.R.string.error_unknown
            else -> 0
        }
    }
}