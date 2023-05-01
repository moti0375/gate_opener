package com.bartovapps.gate_opener.core.alarm

import android.app.AlarmManager
import android.content.Context
import android.util.Log
import com.bartovapps.gate_opener.core.geofence.GateGeofenceService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.System.currentTimeMillis
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmManagerActivator @Inject constructor(private val alarmManager: AlarmManager, @ApplicationContext private val context: Context) : AlarmScheduler {

    private val geofencePendingIntent = GateGeofenceService.getPendingIntent(context)

    override fun scheduleAlarm(schedule: Long) {
        Log.i(TAG, "scheduleAlarm: schedule: $schedule")
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, currentTimeMillis() + schedule, geofencePendingIntent)
    }

    override fun cancel() {
        Log.i(TAG, "cancel")
       // alarmManager.cancel(geofencePendingIntent)
    }

    companion object{
        private const val TAG = "XXX: AlarmActivator"
    }

}