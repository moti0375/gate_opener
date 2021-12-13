package com.bartovapps.gate_opener.core.alarm

import android.app.AlarmManager
import android.content.Context
import android.util.Log
import com.bartovapps.gate_opener.core.geofence.GateGeofenceService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmManagerActivator @Inject constructor(private val alarmManager: AlarmManager, @ApplicationContext private val context: Context) : AlarmScheduler {

    companion object{
        private const val TAG = "XXX: AlarmActivator"
    }

    override fun scheduleAlarm(schedule: Long) {
        Log.i(TAG, "scheduleAlarm: schedule: $schedule")
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + schedule, GateGeofenceService.getPendingIntent(context))
    }

    override fun cancel() {
        Log.i(TAG, "cancel")
        alarmManager.cancel(GateGeofenceService.getPendingIntent(context))
    }
}