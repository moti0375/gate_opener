package com.bartovapps.gate_opener

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.bartovapps.gate_opener.core.activity_detector.ActivityDetector
import dagger.hilt.android.HiltAndroidApp
import io.flutter.app.FlutterApplication
import io.flutter.embedding.android.FlutterActivity
import javax.inject.Inject

@HiltAndroidApp
class App : FlutterApplication(), Configuration.Provider{

    @Inject
    lateinit var activityDetector: ActivityDetector
    @Inject lateinit var workerFactory: HiltWorkerFactory



    override fun onCreate() {
        super.onCreate()
        activityDetector.start()
    }

    override fun getWorkManagerConfiguration(): Configuration  = Configuration.Builder().setWorkerFactory(workerFactory).build()
}