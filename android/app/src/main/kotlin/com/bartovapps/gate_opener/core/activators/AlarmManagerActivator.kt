package com.bartovapps.gate_opener.core.activators

import android.app.AlarmManager
import android.content.Context
import android.util.Log
import com.bartovapps.gate_opener.core.geofence.GateAlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmManagerActivator @Inject constructor(private val alarmManager: AlarmManager, @ApplicationContext private val context: Context) : Activator {
    override fun activate() {
        Log.i("AlarmActivator", "activate")
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), REPEAT_INTERVAL, GateAlarmReceiver.getPendingIntent(context))
    }

    override fun deactivate() {
        Log.i("AlarmActivator", "deactivate")
        alarmManager.cancel(GateAlarmReceiver.getPendingIntent(context))
    }

    companion object{
        private const val REPEAT_INTERVAL = (60 * 1000L)
    }
}