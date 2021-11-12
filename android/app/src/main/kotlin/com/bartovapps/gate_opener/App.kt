package com.bartovapps.gate_opener

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.bartovapps.gate_opener.core.manager.GateOpenerManager
import dagger.hilt.android.HiltAndroidApp
import io.flutter.app.FlutterApplication
import javax.inject.Inject

@HiltAndroidApp
class App : FlutterApplication(), Configuration.Provider{


    @Inject lateinit var gateOpenerManager: GateOpenerManager
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        gateOpenerManager.start()
    }

    override fun getWorkManagerConfiguration(): Configuration  = Configuration.Builder().setWorkerFactory(workerFactory).build()
}