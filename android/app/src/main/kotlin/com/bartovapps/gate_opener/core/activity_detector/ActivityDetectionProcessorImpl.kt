package com.bartovapps.gate_opener.core.activity_detector

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bartovapps.gate_opener.core.GateOpenerService
import com.bartovapps.gate_opener.storage.gates.GatesDao
import com.google.android.gms.location.DetectedActivity.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

import com.bartovapps.gate_opener.core.geofence.GatesGeofenceReceiver
import com.bartovapps.gate_opener.model.Gate
import com.google.android.gms.location.*
import com.google.android.gms.location.Geofence.NEVER_EXPIRE
import android.content.Context.ALARM_SERVICE

import androidx.core.content.ContextCompat.getSystemService
import com.bartovapps.gate_opener.core.activators.Activator
import com.bartovapps.gate_opener.core.activators.AlarmManagerActivator
import com.bartovapps.gate_opener.core.geofence.GateAlarmReceiver
import com.bartovapps.gate_opener.core.location.LocationForegroundService
import com.bartovapps.gate_opener.di.QAlarmManagerActivator
import com.google.android.gms.location.Geofence





@Singleton
class ActivityDetectionProcessorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: GatesDao,
    private val geofencingClient: GeofencingClient,
    @QAlarmManagerActivator private val alarmManagerActivator: Activator
) : ActivityDetectionProcessor {

    private val availableGates = mutableListOf<Gate>()
    private var started = false

    init {
        observeGatesDao()
    }

    private fun observeGatesDao() {
        dao.getAll().observeForever {
            availableGates.clear()
            availableGates.addAll(it)
        }
    }

    override fun onActivitiesDetected(detectedActivities: List<DetectedActivity>) {
        val detectedActivity: DetectedActivity? = detectedActivities.firstOrNull { act -> act.confidence > 75 }
        Log.i(TAG, "processDetectedActivities: $detectedActivity")
//        started = if(detectedActivity?.type == STILL){
//            if(!started){
//                handleVehicleTransitionChange(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
//            }
//            true
//        } else {
//            stopGatesGeofence()
//            false
//        }
    }

    override fun onActivityTransition(transitionResult: ActivityTransitionResult?) {
        Log.i(TAG, "onActivityEntered: ${transitionResult?.transitionEvents}")
        transitionResult?.transitionEvents?.forEach {
            when (it.activityType) {
                WALKING -> Log.i(TAG, "Walking: ${it.transitionType}")
                STILL -> Log.i(TAG, "Still: ${it.transitionType}")
                IN_VEHICLE -> handleVehicleTransitionChange(it.transitionType)
            }
        }
    }

    private fun handleVehicleTransitionChange(transitionType: Int) {
       if(transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER && availableGates.isNotEmpty()){
            Log.i(TAG, "handleVehicleTransitionChange: Entered vehicle: ")
            startGatesGeofence(availableGates)
        } else {
           stopServices()
        }

    }

    @SuppressLint("MissingPermission")
    private fun startGatesGeofence(gates: List<Gate>) {
        alarmManagerActivator.activate()
        Log.i(TAG, "startGateGeofence: locations: $gates")
    }

    private fun stopServices() {
        stopAlarmManager()
        val intent = Intent(context, GateOpenerService::class.java)
        context.stopService(intent)
        val geofenceIntent = Intent(context, LocationForegroundService::class.java)
        context.stopService(intent)
        context.stopService(geofenceIntent)
    }

    private fun stopAlarmManager() {
        alarmManagerActivator.deactivate()
    }

    companion object {
        private const val TAG = "ActivityProcessor"
    }
}