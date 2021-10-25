package com.bartovapps.gate_opener

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bartovapps.gate_opener.core.GateOpenerService
import com.bartovapps.gate_opener.model.serializeToMap
import dagger.hilt.android.AndroidEntryPoint
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.plugin.common.EventChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine

private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : FlutterFragmentActivity(), EventChannel.StreamHandler {
    private val EVENT_CHANNEL = "com.bartovapps.gate_opener.channel.events"
    private val METHOD_CHANNEL = "com.bartovapps.gate_opener.channel"

    private val viewModel by viewModels<MainViewModel>()
    private var eventSink: EventChannel.EventSink? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        Log.i(TAG, "configureFlutterEngine:")
        val event = EventChannel(flutterEngine.dartExecutor.binaryMessenger, EVENT_CHANNEL)
        event.setStreamHandler(this)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, METHOD_CHANNEL).setMethodCallHandler { call, result ->
            Log.i(TAG, "onMethodChannel called: ${call.method}")
            when (call.method) {
                "deleteGate" -> {
                    viewModel.dispatchEvent(DeleteGate(params = call.arguments as Map<String, Any>))
                    result.success(null)
                }
                "createGate" -> {
                    viewModel.dispatchEvent(CreateGate(params = call.arguments as Map<String, Any>))
                    result.success(null)
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }



    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        Log.i(TAG, "onListen: $events")
        eventSink = events
        observeViewModel()
//        val gate = mutableMapOf<String, Any>()
//        val location = mutableMapOf<String, Double>()
//        gate["\"name\""] = "\"Parking Gate\""
//        location["\"latitude\""] = 32.54
//        location["\"longitude\""] = 34.65
//        gate["\"location\""] = location
//        gate["\"phoneNumber\""] = "\"0546798123\""
//
//        val gates = mutableListOf<Map<String, Any>>()
//        gates.add(gate)
       // events?.success(gates)
    }

    private fun observeViewModel() {
        viewModel.gatesMutableLiveData.observe(this, {
           val data = it.map { gate -> gate.serializeToMap() }
           Log.i(TAG, "onChange: $data")
           eventSink?.success(data)
        })
    }

    override fun onCancel(arguments: Any?) {
        eventSink = null
    }
}


