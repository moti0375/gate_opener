package com.bartovapps.gate_opener.core.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.work.impl.Scheduler
import com.bartovapps.gate_opener.analytics.event.GeofenceEvent
import com.bartovapps.gate_opener.analytics.manager.Analytics
import com.bartovapps.gate_opener.core.alarm.AlarmScheduleCalculator
import com.bartovapps.gate_opener.core.manager.GateOpenerManager
import com.bartovapps.gate_opener.core.manager.GateOpenerManagerImpl.Companion.GATE_OPENER_NOTIFICATION_ID
import com.bartovapps.gate_opener.model.serializeToMap
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

    @Inject
    lateinit var scheduleCalculator: AlarmScheduleCalculator
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
                startLocationListener(this, 2000L, 10.0f)  //Start with 2 seconds 10 meters
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
        job = null
    }

    @SuppressLint("MissingPermission")
    @MainThread
    private fun startLocationListener(context: Context, minTime: Long, minDistance: Float) {
            if (PermissionsHelper.isLocationGranted(context)) {
                stopLocationListener()
                CoroutineScope(Dispatchers.Main).launch {
                    val request = com.google.android.gms.location.LocationRequest.create().apply {
                        numUpdates = 1

                    }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this@GateGeofenceService)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")

        analytics.sendEvent(GeofenceEvent(eventName = GeofenceEvent.EVENT_NAME.GEOFENCE_SERVICE_STOPPED))
        terminate()
    }

    private fun stopLocationListener() {
        locationManager.removeUpdates(this)
    }


    override fun onLocationChanged(location: Location) {
        Log.i(TAG, "onLocationChanged: $location")
        if (gateOpenerManager.active) {
            processLocation(location)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}


    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    private fun processLocation(location: Location) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            val nearestGate = gateOpenerManager.getNearestGate(location)
            Log.i(TAG, "checkForClosestGate: $nearestGate")
            nearestGate?.let {
                if(it.second < GEOFENCE_ENTER_RADIUS){
                    Log.i(TAG, "Entered geofence of: ${nearestGate.first.name}")
                    analytics.sendEvent(GeofenceEvent(eventName = GeofenceEvent.EVENT_NAME.ENTERED_GEOFENCE).setDetails(it.first.toBundle()))
                    gateOpenerManager.onGettingCloseToNearGate()
                } else {
                    val nearestGateLocation = Location("Gate").apply {
                        latitude = it.first.location.latitude
                        longitude = it.first.location.longitude
                    }
                    val minTime = scheduleCalculator.calculateAlarmSchedule(location, nearestGateLocation)
                    val minDistance = nearestGateLocation.distanceTo(location) / 2 //set the distance to middle of the distance from nearest gate
                    Log.i(TAG, "Reschedule location updates: minTime: $minTime, minDistance: $minDistance")
                    delay(minTime)
                    startLocationListener(context = this@GateGeofenceService, minTime, minDistance)
                }
            }
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