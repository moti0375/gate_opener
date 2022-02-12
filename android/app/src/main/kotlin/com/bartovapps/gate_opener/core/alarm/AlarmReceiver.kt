package com.bartovapps.gate_opener.core.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.bartovapps.gate_opener.analytics.event.ActivityRecognitionEvent
import com.bartovapps.gate_opener.analytics.manager.Analytics
import com.bartovapps.gate_opener.core.geofence.GateGeofenceService
import com.bartovapps.gate_opener.core.manager.GateOpenerManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var manager : GateOpenerManager

    @Inject
    lateinit var analytics: Analytics

    override fun onReceive(context: Context, intent: Intent?) {
        Log.i("XXX: AlarmReceiver", "onReceive: ")
        if(manager.active){
            val serviceIntent  = GateGeofenceService.getLaunchIntent(context)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }

    companion object{
        fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, AlarmReceiver::class.java)
            return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}