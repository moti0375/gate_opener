package com.bartovapps.gate_opener.core.alarm

import android.location.Location
import android.util.Log
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AlarmScheduleCalculatorImpl @Inject constructor(): AlarmScheduleCalculator {

    /**
     *
     * Calculating the next timer schedule time
     * The calculation is based on considering driving at 120Kmh, the schedule will be at half of the distance between the two locations.
     * When driving at 120Kmh, the time to reach destination is half of the speed in minutes:
     * 1. 100Km @ 120kmh, -> time to destination 50min -> schedule = 25min (in milliseconds)
     * 2. 60Km @ 120kmh, -> time to destination 30min -> schedule = 15min (in milliseconds)
     * 3. 30Km @ 120kmh, -> time to destination 15min -> schedule = 7.5min (in milliseconds)
     *
     * if the distance is less then 2km the schedule shall be 1 min (in milliseconds)
     * @param locationA : Location, location A
     * @param locationB: :Location, location B
     *
     */
    override fun calculateAlarmSchedule(locationA: Location, locationB: Location): Long {
        val distance  = locationA.distanceTo(locationB)/1000

        Log.i(TAG, "calculateAlarmSchedule: distance ${distance}Km")

        val timeBetweenLocations = distance/2 //At 120Kmh (in minutes)
        Log.i(TAG, "calculateAlarmSchedule: timeBetweenLocations ${timeBetweenLocations}min")
        val scheduleTimeMinutes = (timeBetweenLocations / 2) //The time which will take to pass half of the distance at 120Kmh (in minutes)
        Log.i(TAG, "calculateAlarmSchedule: scheduleTimeMinutes ${scheduleTimeMinutes}min")
        val scheduleTime = (TimeUnit.MINUTES.toMillis(scheduleTimeMinutes.toLong()))
        Log.i(TAG, "calculateAlarmSchedule: timeToLocation ${scheduleTime}msec")

        return if(scheduleTime < TimeUnit.SECONDS.toMillis(30L)){
            TimeUnit.SECONDS.toMillis(30L)
        } else {
            scheduleTime //Time to next alarm
        }

    }

    companion object{
        private const val TAG = "XXX: AlarmScheduleCalc"
    }
}