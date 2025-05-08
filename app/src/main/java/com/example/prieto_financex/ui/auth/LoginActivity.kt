package com.example.prieto_financex.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.prieto_financex.R
import com.example.prieto_financex.ui.bottom_nav.AppSummaryActivity
import com.example.prieto_financex.viewmodel.auth.LoginViewModel
import com.google.firebase.FirebaseApp

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Verify Firebase initialization
        if (FirebaseApp.getApps(this).isEmpty()) {
            Toast.makeText(this, "Firebase not initialized!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        val emailET = findViewById<EditText>(R.id.etEmail)
        val passwordET = findViewById<EditText>(R.id.etPassword)
        val loginBtn = findViewById<Button>(R.id.btn_login)
        val forgotPassTxtView = findViewById<TextView>(R.id.forgotPassword)
        val signUpBtn = findViewById<TextView>(R.id.signUpTxtView)

        if (viewModel.isUserAlreadyLoggedIn()) {
            startActivity(Intent(this, AppSummaryActivity::class.java))
            finish()
            return
        }

        forgotPassTxtView.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        signUpBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginBtn.setOnClickListener {
            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.loginSuccess.observe(this, Observer { success ->
            if (success) {
                startActivity(Intent(this, AppSummaryActivity::class.java))
                finish()
            }
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }
}