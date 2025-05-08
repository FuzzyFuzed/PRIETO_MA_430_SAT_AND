package com.example.prieto_financex.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.prieto_financex.R
import com.example.prieto_financex.viewmodel.auth.ForgotPasswordViewModel

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var inputEmail: EditText
    private lateinit var buttonResetPassword: Button

    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        inputEmail = findViewById(R.id.et_email)
        buttonResetPassword = findViewById(R.id.btn_reset_password)

        buttonResetPassword.setOnClickListener {
            val emailAddress = inputEmail.text.toString().trim()

            forgotPasswordViewModel.resetPassword(
                email = emailAddress,
                onSuccess = {
                    Toast.makeText(
                        this,
                        getString(R.string.reset_link_sent, emailAddress),
                        Toast.LENGTH_LONG
                    ).show()
                },
                onError = { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}