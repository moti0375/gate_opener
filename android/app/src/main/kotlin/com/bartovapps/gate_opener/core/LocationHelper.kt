package com.bartovapps.gate_opener.core
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bartovapps.gate_opener.utils.PermissionsHelper
import com.bartovapps.gate_opener.core.GateOpenerService.Companion.FOREGROUND_SERVICE_ID
import com.bartovapps.gate_opener.core.dialer.Dialer
import com.bartovapps.gate_opener.model.ActivityState
import com.bartovapps.gate_opener.utils.FG_CHANNEL
import com.bartovapps.gate_opener.utils.kmhToMsec
import dagger.hilt.android.qualifiers.ApplicationContext


private const val TAG = "com.bartovapps.gate_opener.core.LocationHelper"

class LocationHelper constructor(@ApplicationContext private val context: Context, private val caller: Dialer) : LocationListener {
    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var insideGeofence = true
    private var activityState = ActivityState.STATIONARY
    private val gateLocation = Location("Gate").apply {
        latitude = 31.969825161735752
        longitude = 34.82419797890718
    }

    @SuppressLint("MissingPermission")
    fun startListenToLocationUpdates() {
        if(PermissionsHelper.isLocationGranted(context)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2.0f, this)
        }
    }

    private fun stopListenToLocationUpdates() {
        locationManager.removeUpdates(this)
    }
    override fun onLocationChanged(location: Location) {
        processLocation(location)
    }

    private fun processLocation(location: Location) {
        Log.i(TAG, "process: location: accuracy: ${location.accuracy}, speed: ${location.speed}")
        if (location.hasAccuracy() && location.hasSpeed()) {
            if (location.accuracy < 15 && location.speed > kmhToMsec(20)) {
                //updateActivityState(ActivityState.DRIVING)
                updateNotification("Driving.. distance: ${location.distanceTo(gateLocation)}m")
                insideGeofence = if (location.distanceTo(gateLocation) < 50) {
                    if (!insideGeofence) {
                        stopListenToLocationUpdates()
                        updateNotification("Entered geofence..")
                        makeCall()
                    }
                    true
                } else {
                    if (insideGeofence) {
                        updateNotification("Exit area")
                    }
                    false
                }
            }
        } else if (location.speed == 0.0f) {
            updateActivityState(ActivityState.STATIONARY)
        }
    }

    private fun updateActivityState(activityState: ActivityState) {
        if (activityState != this.activityState) {
            this.activityState
            when (activityState) {
                ActivityState.STATIONARY -> updateNotification("Standing still..")
                ActivityState.DRIVING -> updateNotification("Driving..")
                else -> {}
            }
        }

    }

    private fun updateNotification(message: String) {
        val builder = NotificationCompat.Builder(context, FG_CHANNEL)
            .setSmallIcon(com.bartovapps.gate_opener.R.mipmap.ic_launcher)
            .setContentText(message)
        mNotificationManager.notify(FOREGROUND_SERVICE_ID, builder.build())
    }

    private fun makeCall() {
        updateNotification("Calling gate..")
        Log.i(TAG, "Calling gate!!")
        caller.makeCall("+972544255962")
    }

}
