package com.bartovapps.gate_opener.core.geofence

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.bartovapps.gate_opener.core.GateOpenerService
import com.bartovapps.gate_opener.core.manager.GateOpenerManager
import com.bartovapps.gate_opener.utils.PermissionsHelper
import com.bartovapps.gate_opener.utils.createAppNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GateGeofenceService : Service(), LocationListener {

    @Inject
    lateinit var locationManager : LocationManager
    @Inject
    lateinit var gateOpenerManager: GateOpenerManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf(startId)
            return START_NOT_STICKY
        }
        val action = intent.action
        if (action == null) {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        return handleStartCommand(action)
    }

    private fun handleStartCommand(action: String) : Int{
        Log.i(TAG, "handleStartCommand: action: $action")
        return when (action) {
            ACTION_START -> {
                val notification = createAppNotification(context = this.applicationContext)
                startForeground(GateOpenerService.FOREGROUND_SERVICE_ID, notification)
                startLocationListener(this)
                START_STICKY
            }
            else -> {
                stopLocationListener()
                stopSelf() // Stop all the instances
                START_NOT_STICKY
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationListener(context: Context) {
        if(PermissionsHelper.isLocationGranted(context)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10.0f, this)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
        stopLocationListener()
    }

    private fun stopLocationListener() {
        locationManager.removeUpdates(this)
    }


    override fun onLocationChanged(location: Location) {
        Log.i(TAG, "onLocationChanged: $location")
        checkForClosestGate(location)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}


    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    private fun checkForClosestGate(location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
           val closestGate = gateOpenerManager.getNearestGate(location)
            Log.i(TAG, "checkForClosestGate: $closestGate")
            closestGate?.let {
                if(it.second < GEOFENCE_ENTER_RADIUS) {
                    gateOpenerManager.onGettingCloseToNearGate()
                }
            }
            stopSelf()
        }
    }

    companion object{
        private const val TAG = "LocationForegroundService"
        private const val ACTION_START = "com.bartovapps.gate_opener.core.location.LocationForegroundService.start"
        const val GEOFENCE_ENTER_RADIUS = 1000
        const val GEOFENCE_EXIT_FACTOR = 1.25

        fun sendStartIntent(context: Context) {
            val intent = Intent(context, GateGeofenceService::class.java)
            intent.action = ACTION_START
            safeStartService(context, intent)
        }

        /**
         * Send intent to the service. Supports all android versions
         * @param context
         * @param intent
         */
        private fun safeStartService(context: Context, intent: Intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}