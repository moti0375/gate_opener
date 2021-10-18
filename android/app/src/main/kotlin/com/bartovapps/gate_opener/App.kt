package com.bartovapps.gate_opener

import com.bartovapps.gate_opener.core.activity_detector.ActivityDetector
import dagger.hilt.android.HiltAndroidApp
import io.flutter.app.FlutterApplication
import io.flutter.embedding.android.FlutterActivity
import javax.inject.Inject

@HiltAndroidApp
class App : FlutterApplication(){

    @Inject
    lateinit var activityDetector: ActivityDetector

    override fun onCreate() {
        super.onCreate()
        activityDetector.start()
    }
}