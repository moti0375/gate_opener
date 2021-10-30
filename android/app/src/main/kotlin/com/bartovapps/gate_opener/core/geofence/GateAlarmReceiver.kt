package com.bartovapps.gate_opener.core.geofence

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bartovapps.gate_opener.core.location.LocationForegroundService

class GateAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive: ")
        LocationForegroundService.sendStartIntent(context)
    }

    companion object{
        private const val TAG = "GateAlarmReceiver"
        fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, GateAlarmReceiver::class.java)
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }
}