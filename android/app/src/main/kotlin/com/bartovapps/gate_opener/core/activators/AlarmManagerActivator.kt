package com.bartovapps.gate_opener.core.activators

import android.app.AlarmManager
import android.content.Context
import com.bartovapps.gate_opener.core.geofence.GateAlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmManagerActivator @Inject constructor(private val alarmManager: AlarmManager, @ApplicationContext private val context: Context) : Activator {
    override fun isValid(): Boolean = true
    override fun activate() {
        if(isValid()){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), REPEAT_INTERVAL, GateAlarmReceiver.getPendingIntent(context))
        }
    }

    override fun deactivate() {
        alarmManager.cancel(GateAlarmReceiver.getPendingIntent(context))
    }

    override fun getName(): String = "Alarm Manager"

    companion object{
        private const val REPEAT_INTERVAL = (60 * 1000L)
    }
}