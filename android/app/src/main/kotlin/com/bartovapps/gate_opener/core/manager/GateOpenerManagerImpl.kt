package com.bartovapps.gate_opener.core.manager

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import com.bartovapps.gate_opener.analytics.event.ManagerEvent
import com.bartovapps.gate_opener.analytics.manager.Analytics
import com.bartovapps.gate_opener.core.GateOpenerService
import com.bartovapps.gate_opener.core.activators.Activator
import com.bartovapps.gate_opener.core.alarm.AlarmScheduleCalculator
import com.bartovapps.gate_opener.core.alarm.AlarmScheduler
import com.bartovapps.gate_opener.core.geofence.GateGeofenceService
import com.bartovapps.gate_opener.di.QActivityDetectorActivator
import com.bartovapps.gate_opener.model.Gate
import com.bartovapps.gate_opener.storage.gates.GatesDao
import com.bartovapps.gate_opener.utils.verifyMinimumSdk
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GateOpenerManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: GatesDao,
    private val alarmActivator: AlarmScheduler,
    @QActivityDetectorActivator private val activityDetector: Activator,
    private val analytics: Analytics,
    private val scheduleCalculator: AlarmScheduleCalculator
) : GateOpenerManager {

    private val availableGates = mutableListOf<Gate>()

    @Volatile
    override var active: Boolean = false

    override fun start() {
        //analytics.sendEvent(ManagerEvent(eventName = ManagerEvent.EVENT_NAME.STARTED))
        fetchAllGates()
    }

    override fun onEnteredVehicle() {
        active = true
        if (verifyMinimumSdk(Build.VERSION_CODES.S)) {
            scheduleAlarm(10000L)
        } else {
            startGeofenceService()
        }
    }

    private fun startGeofenceService() {
        val serviceIntent = GateGeofenceService.getLaunchIntent(context)
        if (active) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }

    override fun onGettingCloseToNearGate() {
        // stopAlarmManager()
        startGateOpenerService()
        stopGeofenceService()
    }

    override fun onExitNearestGateZone() {
        stopGateOpenerService()
        scheduleAlarm()
    }

    override fun getNearestGate(currentLocation: Location): Pair<Gate, Float>? {
        return availableGates.map {
            Pair(it, Location("gate").apply {
                latitude = it.location.latitude
                longitude = it.location.longitude
            }.distanceTo(currentLocation))
        }.minByOrNull {
            it.second
        }
    }

    override fun noGateFoundAtThisLocation(currentLocation: Location) {
        scheduleAlarm(60 * 1000L)
//        val nearestGate = getNearestGate(currentLocation)
//        nearestGate?.let {
//            val schedule = scheduleCalculator.calculateAlarmSchedule(currentLocation, Location("gate").apply {
//                latitude = it.first.location.latitude
//                longitude = it.first.location.longitude
//            })
//            scheduleAlarm(schedule)
//        } ?: run {
//            scheduleAlarm(60 * 1000L)
//        }
    }

    override fun stopTracking() {
        Log.i(TAG, "stopTracking: ")
        activityDetector.deactivate()
        onExitVehicle()
    }

    override fun onExitVehicle() {
        Log.i(TAG, "onExitVehicle: stop everything!!")
        active = false
        stopAlarmManager()
        stopGeofenceService()
        stopGateOpenerService()
    }

    override fun onReachedDestination() {
        stopGateOpenerService()
        scheduleAlarm()
    }

    override fun onGatesUpdated() {
        fetchAllGates()
    }

    private fun startGateOpenerService() {
        GateOpenerService.sendStartIntent(context)
    }

    private fun stopGateOpenerService() {
        val intent = Intent(context, GateOpenerService::class.java)
        context.stopService(intent)
    }

    private fun stopGeofenceService() {
        val geofenceIntent = Intent(context, GateGeofenceService::class.java)
        geofenceIntent.action = "STOP_SERVICE"
        context.stopService(geofenceIntent)
    }

    private fun scheduleAlarm(scheduleTime: Long = 60 * 1000L) {
        alarmActivator.scheduleAlarm(scheduleTime)
    }

    private fun stopAlarmManager() {
        alarmActivator.cancel()
    }


    private fun fetchAllGates() {
        CoroutineScope(Dispatchers.IO).launch {
            val gates = dao.getAllGates()
            if (gates.isEmpty()) {
                stopTracking()
            } else {
                availableGates.clear()
                availableGates.addAll(gates)
                activityDetector.activate()
            }
        }
    }

    companion object {
        private const val TAG = "GateOpenerManager"
        const val GATE_OPENER_NOTIFICATION_ID = 0xF91C0FE
    }
}