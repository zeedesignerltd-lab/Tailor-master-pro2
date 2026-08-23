package com.tailormaster.pro

import android.app.Application
import com.google.firebase.FirebaseApp
import com.tailormaster.pro.data.AuthRepository

class TailorApp : Application() {
    lateinit var authRepository: AuthRepository
        private set

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        authRepository = AuthRepository()
    }
}
