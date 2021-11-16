package com.bartovapps.gate_opener.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bartovapps.gate_opener.R
import com.bartovapps.gate_opener.analytics.event.Event
import com.bartovapps.gate_opener.analytics.event.GeofenceEvent
import com.bartovapps.gate_opener.analytics.manager.Analytics
import com.bartovapps.gate_opener.utils.PermissionsHelper
import com.bartovapps.gate_opener.core.GateOpenerService.Companion.FOREGROUND_SERVICE_ID
import com.bartovapps.gate_opener.core.dialer.Dialer
import com.bartovapps.gate_opener.core.geofence.GateGeofenceService.Companion.GEOFENCE_ENTER_RADIUS
import com.bartovapps.gate_opener.core.geofence.GateGeofenceService.Companion.GEOFENCE_EXIT_FACTOR
import com.bartovapps.gate_opener.core.manager.GateOpenerManager
import com.bartovapps.gate_opener.model.Gate
import com.bartovapps.gate_opener.utils.FG_CHANNEL
import com.bartovapps.gate_opener.utils.kmhToMsec
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


private const val TAG = "com.bartovapps.gate_opener.core.LocationHelper"

class LocationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val caller: Dialer,
    private val gateOpenerManager: GateOpenerManager,
    private val analytics: Analytics
) : LocationListener {
    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var insideGeofence = true

    @SuppressLint("MissingPermission")
    fun startListenToLocationUpdates() {
        if (PermissionsHelper.isLocationGranted(context)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2.0f, this)
        }
    }

    fun stopListenToLocationUpdates() {
        locationManager.removeUpdates(this)
    }


    override fun onLocationChanged(location: Location) {
        processLocation(location)
    }


    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}

    private fun processLocation(location: Location) {
        Log.i(TAG, "process: location: accuracy: ${location.accuracy}, speed: ${location.speed}")
        if (location.hasAccuracy() && location.hasSpeed()) {
            val closestGate = gateOpenerManager.getNearestGate(location)?.first
            closestGate?.let {
                val gateLocation = Location("Gate").apply {
                    latitude = it.location.latitude
                    longitude = it.location.longitude
                }
                val distance = location.distanceTo(gateLocation)

                if (distance > GEOFENCE_ENTER_RADIUS * GEOFENCE_EXIT_FACTOR) { //It means we're leaving the nearest gate..
                    gateOpenerManager.onExitNearestGateZone()
                    analytics.sendEvent(GeofenceEvent(eventName = GeofenceEvent.EVENT_NAME.EXIT_GEOFENCE))
                    return
                }

                if (location.accuracy <= MINIMUM_ACCURACY && location.speed >= kmhToMsec(OPEN_MIN_SPEED) && location.speed <= kmhToMsec(OPEN_MAX_SPEED)) {
                    insideGeofence = if (distance < OPEN_TRIGGER_DISTANCE) {
                        if (!insideGeofence) { //Entered near gate!! Open it!!
                            makeCall(it)
                            //gateOpenerManager.onReachedDestination()
                            analytics.sendEvent(GeofenceEvent(eventName = GeofenceEvent.EVENT_NAME.ARRIVED_DESTINATION).setDetails(it.toBundle()))
                        } else {
                            updateNotification("Reaching: ${it.name} in ${distance}m")
                        }
                        true
                    } else {
                        if (insideGeofence) {
                            updateNotification("Around ${it.name}")
                        }
                        false
                    }
                } else {
                    updateNotification("Driving to ${it.name}: - ${distance}m")
                }
            }
        }
    }

    private fun updateNotification(message: String) {
        val builder = NotificationCompat.Builder(context, FG_CHANNEL)
            .setSmallIcon(R.drawable.ic_parking_barrier)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_parking_barrier))
            .setContentText(message)
        mNotificationManager.notify(FOREGROUND_SERVICE_ID, builder.build())
    }

    private fun makeCall(gate: Gate) {
        updateNotification("Calling ${gate.name}")
        caller.makeCall(gate.phoneNumber)
    }

    companion object {
        private const val MINIMUM_ACCURACY = 25
        private const val OPEN_MIN_SPEED = 15
        private const val OPEN_MAX_SPEED = 45
        private const val OPEN_TRIGGER_DISTANCE = 50
    }
}
