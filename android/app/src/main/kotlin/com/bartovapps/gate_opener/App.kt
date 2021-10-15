package com.bartovapps.gate_opener

import android.app.Application
import com.bartovapps.gate_opener.core.activity_detector.ActivityDetector
import dagger.hilt.android.HiltAndroidApp
import io.flutter.app.FlutterApplication
import javax.inject.Inject

@HiltAndroidApp
class App : Application(){

    @Inject
    lateinit var activityDetector: ActivityDetector

    override fun onCreate() {
        super.onCreate()
        activityDetector.start()
    }
}