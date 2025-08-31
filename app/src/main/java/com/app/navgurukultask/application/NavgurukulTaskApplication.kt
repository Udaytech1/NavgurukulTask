package com.app.navgurukultask.application

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NavgurukulTaskApplication : Application() {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        
        FirebaseApp.initializeApp(this)

        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(workerFactory).build())

    }
}
