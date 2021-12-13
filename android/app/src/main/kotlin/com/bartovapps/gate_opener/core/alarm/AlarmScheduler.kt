package com.bartovapps.gate_opener.core.alarm

const val ALARM_DEFAULT_INTERVAL = (60 * 1000L)
interface AlarmScheduler {
    fun scheduleAlarm(schedule: Long = ALARM_DEFAULT_INTERVAL)
    fun cancel()
}