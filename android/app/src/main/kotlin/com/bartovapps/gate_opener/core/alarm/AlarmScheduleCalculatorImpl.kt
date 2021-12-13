package com.bartovapps.gate_opener.core.alarm

import android.location.Location
import android.util.Log
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
        val distance = locationA.distanceTo(locationB)
        val fullKms : Double = (distance/1000).toDouble()

        Log.i(TAG, "calculateAlarmSchedule: distance ${fullKms}Km")
        return if(fullKms > 0 && fullKms < 1.0) { //Less then 1 km, make it every minute
            Log.i(TAG, "calculateAlarmSchedule: getting closer.. ${ONE_MINUTE_IN_MILLISECONDS}msec")
            ONE_MINUTE_IN_MILLISECONDS
        } else {
            //Time to location if driving 120Kmh
                val timeBetweenLocations = fullKms/2
            Log.i(TAG, "calculateAlarmSchedule: timeBetweenLocations ${timeBetweenLocations}min")
            val scheduleTimeMinutes = (timeBetweenLocations / 2)
            Log.i(TAG, "calculateAlarmSchedule: scheduleTimeMinutes ${scheduleTimeMinutes}min")
            val scheduleTime = (scheduleTimeMinutes * ONE_MINUTE_IN_MILLISECONDS).toLong()
            Log.i(TAG, "calculateAlarmSchedule: timeToLocation ${scheduleTime} msec")
            scheduleTime //Time to next alarm
        }
    }

    companion object{
        const val ONE_MINUTE_IN_MILLISECONDS = 60 * 1000L
        private const val TAG = "XXX: AlarmScheduleCalc"
    }
}