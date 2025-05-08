package com.example.prieto_financex.viewmodel.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.prieto_financex.R
import com.example.prieto_financex.data.locals.AppDatabaseHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(app: Application) : AndroidViewModel(app) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val databaseHelper = AppDatabaseHelper(app)
    private val context = getApplication<Application>()

    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isEmpty()) {
            onError(context.getString(R.string.enter_email))
            return
        }

        viewModelScope.launch {
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = databaseHelper.updateVerificationStatus(email, false)
                        // Optionally handle `result` if you want to track rows affected
                        onSuccess()
                    } else {
                        val message = task.exception?.message
                            ?: context.getString(R.string.error_unknown)
                        val errorText = context.getString(R.string.reset_failed, message)
                        onError(errorText)
                    }
                }
        }
    }
}