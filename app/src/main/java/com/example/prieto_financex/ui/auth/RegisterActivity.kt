package com.example.prieto_financex.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.prieto_financex.R
import com.example.prieto_financex.viewmodel.auth.RegisterViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var registerBtn: Button

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase safely
        initializeFirebase()

        initViews()
        setupListeners()
        setupObservers()
    }

    @OptIn(UnstableApi::class)
    private fun initializeFirebase() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                Firebase.initialize(this)
                // Verify initialization
                if (FirebaseApp.getApps(this).isNotEmpty()) {
                    Log.d("FirebaseInit", "Firebase initialized successfully")
                } else {
                    Log.e("FirebaseInit", "Firebase initialization failed")
                    showError("Firebase initialization failed. Please restart the app.")
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Error initializing Firebase", e)
            showError("Failed to initialize app services. Please try again.")
        }
    }

    private fun initViews() {
        usernameET = findViewById(R.id.etUserName)
        emailET = findViewById(R.id.etEmail)
        passwordET = findViewById(R.id.etPassword)
        registerBtn = findViewById(R.id.registerBtn)
    }

    private fun setupListeners() {
        registerBtn.setOnClickListener {
            val username = usernameET.text.toString().trim()
            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString().trim()

            if (validateInputs(username, email, password)) {
                viewModel.registerUser(username, email, password)
            }
        }
    }

    private fun validateInputs(username: String, email: String, password: String): Boolean {
        return when {
            username.isEmpty() -> {
                usernameET.error = "Username required"
                false
            }
            email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailET.error = "Valid email required"
                false
            }
            password.isEmpty() || password.length < 6 -> {
                passwordET.error = "Password must be at least 6 characters"
                false
            }
            else -> true
        }
    }

    private fun setupObservers() {
        viewModel.registrationSuccess.observe(this, Observer { success ->
            if (success) {
                startActivity(Intent(this, EmailVerificationActivity::class.java))
                finish()
            }
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            showError(message)
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            registerBtn.isEnabled = !isLoading
            registerBtn.text = if (isLoading) "Processing..." else "REGISTER"
        })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}