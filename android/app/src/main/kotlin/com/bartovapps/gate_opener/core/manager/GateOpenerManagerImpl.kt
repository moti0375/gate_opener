package com.bartovapps.gate_opener.core.manager

import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.bartovapps.gate_opener.core.GateOpenerService
import com.bartovapps.gate_opener.core.activators.Activator
import com.bartovapps.gate_opener.core.location.LocationForegroundService
import com.bartovapps.gate_opener.di.QActivityDetectorActivator
import com.bartovapps.gate_opener.di.QAlarmManagerActivator
import com.bartovapps.gate_opener.model.Gate
import com.bartovapps.gate_opener.storage.gates.GatesDao
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GateOpenerManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: GatesDao,
    @QAlarmManagerActivator private val alarmActivator: Activator,
    @QActivityDetectorActivator private val activityDetectorImpl: Activator
) : GateOpenerManager {

    private val availableGates = mutableListOf<Gate>()

    override fun start() {
        dao.getAll().observeForever {
            if (it.isNotEmpty()) {
                activityDetectorImpl.activate()
                availableGates.clear()
                availableGates.addAll(it)
            } else {
                availableGates.clear()
                stopTracking()
            }
        }
    }

    override fun onEnteredVehicle() {
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


    override fun stopTracking() {
        activityDetectorImpl.deactivate()
        onExitVehicle()
    }

    override fun onExitVehicle() {
        Log.i(TAG, "onExitVehicle: stop everything!!")
        stopAlarmManager()
        stopForegroundService()
        stopGateOpenerService()
    }

    override fun onReachedDestination() {
        stopGateOpenerService()
        startAlarmManager()
    }

    private fun startGateOpenerService() {
        GateOpenerService.sendStartIntent(context)
    }

    private fun stopGateOpenerService() {
        val intent = Intent(context, GateOpenerService::class.java)
        context.stopService(intent)
    }

    private fun stopForegroundService() {
        val geofenceIntent = Intent(context, LocationForegroundService::class.java)
        context.stopService(geofenceIntent)
    }

    private fun startAlarmManager(){
        alarmActivator.activate()
    }

    private fun stopAlarmManager(){
        alarmActivator.deactivate()
    }


    companion object{
        private const val TAG = "GateOpenerMangaer"
    }
}