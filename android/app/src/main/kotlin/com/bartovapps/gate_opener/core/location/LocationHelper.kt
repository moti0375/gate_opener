package com.bartovapps.gate_opener.core.location
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
import com.bartovapps.gate_opener.model.Gate
import com.bartovapps.gate_opener.storage.gates.GatesDao
import com.bartovapps.gate_opener.utils.FG_CHANNEL
import com.bartovapps.gate_opener.utils.kmhToMsec
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "com.bartovapps.gate_opener.core.LocationHelper"

class LocationHelper @Inject constructor(@ApplicationContext private val context: Context, private val caller: Dialer, private val dao: GatesDao) : LocationListener {
    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var insideGeofence = true
    private var activityState = ActivityState.STATIONARY
    private var closestGate : Gate? = null
    private val availableGates = mutableListOf<Gate>()

    init {
        observeDao()
    }

    private fun observeDao() {
        CoroutineScope(Dispatchers.IO).launch {
            val gates = dao.getAllGates()
            availableGates.addAll(gates)
        }
    }

//    private val gateLocation = Location("Gate").apply {
//        latitude = 31.969825161735752
//        longitude = 34.82419797890718
//    }

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
        checkClosestGateDistance(location = location)
        processLocation(location)
    }


    private fun checkClosestGateDistance(location: Location) {
        val closestGate: Pair<Gate, Float>? = location.let { lastLocation ->
            availableGates.map {
                Pair(it, Location("Gate").apply {
                    latitude = it.location.latitude
                    longitude = it.location.longitude
                }.distanceTo(lastLocation))
            }.minByOrNull {
                it.second
            }
        }
        Log.i(TAG, "Closest gate: $closestGate")
        this.closestGate = closestGate?.first
    }

    private fun processLocation(location: Location) {
        Log.i(TAG, "process: location: accuracy: ${location.accuracy}, speed: ${location.speed}")
        if (location.hasAccuracy() && location.hasSpeed()) {
            if (location.accuracy < 15 && location.speed > kmhToMsec(20)) {
                    closestGate?.let {
                        val gateLocation = Location("Gate").apply {
                            latitude = it.location.latitude
                            longitude = it.location.longitude
                        }
                        updateNotification("Driving.. distance to ${it.name}: ${location.distanceTo(gateLocation)}m")
                        insideGeofence = if (location.distanceTo(gateLocation) < 50) {
                            if (!insideGeofence) {
                                stopListenToLocationUpdates()
                                updateNotification("Getting close to: ${it.name}")
                                makeCall(it)
                            }
                            true
                        } else {
                            if (insideGeofence) {
                                updateNotification("Exit area")
                            }
                            false
                        }
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

    private fun makeCall(gate: Gate) {
        updateNotification("Calling ${gate.name}")
        caller.makeCall(gate.phoneNumber)
    }

}
