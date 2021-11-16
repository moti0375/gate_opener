package com.bartovapps.gate_opener.core.geofence

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bartovapps.gate_opener.core.manager.GateOpenerManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GateAlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var manager: GateOpenerManager

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive: ")
        if(manager.active){
            GateGeofenceService.sendStartIntent(context)
        }
    }

    companion object{
        private const val TAG = "GateAlarmReceiver"
        fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, GateAlarmReceiver::class.java)
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}