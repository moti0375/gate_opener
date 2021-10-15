package com.bartovapps.gate_opener

import android.content.Intent
import android.util.Log
import com.bartovapps.gate_opener.core.GateOpenerService
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.bartovapps.gate_opener.flutter.dev/channel"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->

            Log.i("MainActivity", "onMethodChannel called: ${call.method}")

            when (call.method) {
                "startService" -> {
                    GateOpenerService.sendStartIntent(this)
                }
                "stopService" -> {
                    val intent = Intent(this, GateOpenerService::class.java)
                    stopService(intent)
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }
}


