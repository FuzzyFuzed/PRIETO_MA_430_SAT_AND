package com.example.prieto_financex.core

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("FirebaseInit", "Initializing Firebase...")
        FirebaseApp.initializeApp(this)
        Log.d("FirebaseInit", "Firebase initialized successfully")
    }
}