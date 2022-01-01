package com.bartovapps.gate_opener.core.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
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
import com.bartovapps.gate_opener.analytics.event.GeofenceEvent
import com.bartovapps.gate_opener.analytics.manager.Analytics
import com.bartovapps.gate_opener.core.manager.GateOpenerManager
import com.bartovapps.gate_opener.core.manager.GateOpenerManagerImpl.Companion.GATE_OPENER_NOTIFICATION_ID
import com.bartovapps.gate_opener.utils.PermissionsHelper
import com.bartovapps.gate_opener.utils.createAppNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class GateGeofenceService : Service(), LocationListener {

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var gateOpenerManager: GateOpenerManager

    @Inject
    lateinit var analytics: Analytics


    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        val notification = createAppNotification(context = this.applicationContext)
        startForeground(GATE_OPENER_NOTIFICATION_ID, notification)
        analytics.sendEvent(GeofenceEvent(eventName = GeofenceEvent.EVENT_NAME.GEOFENCE_SERVICE_STARTED))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            terminate()
            return START_NOT_STICKY
        }
        val action = intent.action
        if (action == null) {
          terminate()
            return START_NOT_STICKY
        }

        return handleStartCommand(action)
    }

    private fun handleStartCommand(action: String): Int {
        Log.i(TAG, "handleStartCommand: action: $action")
        if(!gateOpenerManager.active){
            terminate()
            return START_NOT_STICKY
        }
        return when (action) {
            ACTION_START -> {
                startLocationListener(this)
                START_STICKY
            }
            else -> {
                terminate()
                START_NOT_STICKY
            }
        }
    }

    private fun terminate(){
        stopLocationListener()
        stopForeground(true)
        stopSelf() // Stop all the instances
        job?.cancel()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationListener(context: Context) {
        if (PermissionsHelper.isLocationGranted(context)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10.0f, this)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
        analytics.sendEvent(GeofenceEvent(eventName = GeofenceEvent.EVENT_NAME.GEOFENCE_SERVICE_STOPPED))
        stopLocationListener()
    }

    private fun stopLocationListener() {
        locationManager.removeUpdates(this)
    }


    override fun onLocationChanged(location: Location) {
        Log.i(TAG, "onLocationChanged: $location")
        if (gateOpenerManager.active) {
            checkForClosestGate(location)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}


    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    private fun checkForClosestGate(location: Location) {
        job = CoroutineScope(Dispatchers.IO).launch {
            val nearestGate = gateOpenerManager.getNearestGate(location)
            Log.i(TAG, "checkForClosestGate: $nearestGate")
            if(nearestGate != null && nearestGate.second < GEOFENCE_ENTER_RADIUS) {
                Log.i(TAG, "Entered geofence of: ${nearestGate.first.name}")
                analytics.sendEvent(GeofenceEvent(eventName = GeofenceEvent.EVENT_NAME.ENTERED_GEOFENCE).setDetails(nearestGate.first.toBundle()))
                gateOpenerManager.onGettingCloseToNearGate()
            } else {
                gateOpenerManager.noGateFoundAtThisLocation(location) //Updating the manager that no gate was found, reschedule the alarm
            }
            terminate()
        }
    }

    companion object {
        private const val TAG = "XXX: GateGeofenceService"
        const val ACTION_START = "GeofenceForegroundService.start"
        const val GEOFENCE_ENTER_RADIUS = 1000
        const val GEOFENCE_EXIT_FACTOR = 1.25

        fun getLaunchIntent(context: Context) : Intent{
            val intent = Intent(context, GateGeofenceService::class.java)
            intent.action = ACTION_START
            return intent
        }

        @SuppressLint("WrongConstant")
        fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, GateGeofenceService::class.java)
            intent.action = ACTION_START
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                PendingIntent.getForegroundService(context, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }
}