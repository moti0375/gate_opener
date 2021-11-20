package com.bartovapps.gate_opener.core.manager

import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.bartovapps.gate_opener.analytics.event.Event
import com.bartovapps.gate_opener.analytics.event.ManagerEvent
import com.bartovapps.gate_opener.analytics.manager.Analytics
import com.bartovapps.gate_opener.core.GateOpenerService
import com.bartovapps.gate_opener.core.activators.Activator
import com.bartovapps.gate_opener.core.geofence.GateGeofenceService
import com.bartovapps.gate_opener.di.QActivityDetectorActivator
import com.bartovapps.gate_opener.di.QAlarmManagerActivator
import com.bartovapps.gate_opener.model.Gate
import com.bartovapps.gate_opener.storage.gates.GatesDao
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
    @QAlarmManagerActivator private val alarmActivator: Activator,
    @QActivityDetectorActivator private val activityDetector: Activator,
    private val analytics: Analytics
) : GateOpenerManager {

    private val availableGates = mutableListOf<Gate>()
    override var active: Boolean = false

    override fun start() {
        analytics.sendEvent(ManagerEvent(eventName = ManagerEvent.EVENT_NAME.STARTED))
        fetchAllGates()
    }

    override fun onEnteredVehicle() {
        active = true
        startAlarmManager()
    }

    override fun onGettingCloseToNearGate() {
        stopAlarmManager()
        startGateOpenerService()
    }

    override fun onExitNearestGateZone() {
        stopGateOpenerService()
        startAlarmManager()
    }

    override fun getNearestGate(currentLocation: Location): Pair<Gate, Float> {
        return availableGates.map {
            Pair(it, Location("gate").apply {
                latitude = it.location.latitude
                longitude = it.location.longitude
            }.distanceTo(currentLocation))
        }.minByOrNull {
            it.second
        }
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
        stopForegroundService()
        stopGateOpenerService()
    }

    override fun onReachedDestination() {
        stopGateOpenerService()
        startAlarmManager()
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

    private fun stopForegroundService() {
        val geofenceIntent = Intent(context, GateGeofenceService::class.java)
        context.stopService(geofenceIntent)
    }

    private fun startAlarmManager(){
        alarmActivator.activate()
    }

    private fun stopAlarmManager(){
        alarmActivator.deactivate()
    }


    private fun fetchAllGates() {
        CoroutineScope(Dispatchers.IO).launch {
            val gates = dao.getAllGates()
            if(gates.isEmpty()){
                stopTracking()
            } else {
                availableGates.clear()
                availableGates.addAll(gates)
                activityDetector.activate()
            }
        }
    }
    companion object{
        private const val TAG = "GateOpenerManager"
    }
}