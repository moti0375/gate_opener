package com.bartovapps.gate_opener.core.alarm

import android.location.Location

interface AlarmScheduleCalculator {
    fun calculateAlarmSchedule(locationA: Location, locationB : Location) : Long
}