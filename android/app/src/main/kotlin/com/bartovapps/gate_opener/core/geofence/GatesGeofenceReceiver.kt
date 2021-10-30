package com.bartovapps.gate_opener.core.geofence

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bartovapps.gate_opener.core.GateOpenerService
import com.bartovapps.gate_opener.storage.gates.GatesDao
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GatesGeofenceReceiver : BroadcastReceiver() {

    @Inject
    lateinit var gatesDao: GatesDao

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive: action: ${intent.action}")
        checkCurrentLocation(context, intent)
    }

    private fun checkCurrentLocation(context: Context, intent: Intent) {
        val geofenceEvent = GeofencingEvent.fromIntent(intent)
        Log.i(TAG, "checkCurrentLocation: ${geofenceEvent.errorCode}")
        if(geofenceEvent.hasError()){
            Log.e(TAG, "Something went wrong: ${GeofenceStatusCodes.getStatusCodeString(geofenceEvent.errorCode)}")
            return
        } else {
            val geofenceTransition = geofenceEvent.geofenceTransition
            Log.i(TAG, "triggerring geofences: ${geofenceEvent.geofenceTransition}")
            if(geofenceTransition == -1){
                val triggeringGeofences = geofenceEvent.triggeringGeofences
                Log.i(TAG, "triggerring geofences: $triggeringGeofences")
                GateOpenerService.sendStartIntent(context)
            }
        }
    }

    companion object {
        private const val TAG = "GatesGeofenceReceiver"
        private const val REQUEST_CODE = 0
        fun createPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, GatesGeofenceReceiver::class.java)
            return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}