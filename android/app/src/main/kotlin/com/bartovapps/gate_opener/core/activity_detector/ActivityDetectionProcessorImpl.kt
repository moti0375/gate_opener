package com.bartovapps.gate_opener.core.activity_detector

import android.util.Log
import com.google.android.gms.location.DetectedActivity.*
import javax.inject.Inject
import javax.inject.Singleton
import com.google.android.gms.location.*
import com.bartovapps.gate_opener.core.manager.GateOpenerManager


@Singleton
class ActivityDetectionProcessorImpl @Inject constructor(
    private val gateOpenerManager: GateOpenerManager
) : ActivityDetectionProcessor {

    private var started = false

    override fun onActivitiesDetected(detectedActivities: List<DetectedActivity>) {
        val detectedActivity: DetectedActivity? = detectedActivities.firstOrNull { act -> act.confidence > 75 }
        Log.i(TAG, "processDetectedActivities: $detectedActivity")
//        started = if(detectedActivity?.type == STILL){
//            if(!started){
//                handleVehicleTransitionChange(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
//            }
//            true
//        } else {
//            gateOpenerManager.onExitVehicle()
//            false
//        }
    }

    override fun onActivityTransition(transitionResult: ActivityTransitionResult?) {
        Log.i(TAG, "onActivityTransition: ${transitionResult?.transitionEvents}")
        transitionResult?.transitionEvents?.forEach {
            when (it.activityType) {
                IN_VEHICLE -> handleVehicleTransitionChange(it.transitionType)
                else -> {
                    if(it.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER){ //Starting other activity other than vehicle
                        gateOpenerManager.onExitVehicle()
                    }
                }
            }
        }
    }

    private fun handleVehicleTransitionChange(transitionType: Int) {
        Log.i(TAG, "handleVehicleTransitionChange: Entered vehicle: ")
        if(transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER){
            Log.i(TAG, "handleVehicleTransitionChange: Entered vehicle: ")
            gateOpenerManager.onEnteredVehicle() //Starting everything..
        } else {
            gateOpenerManager.onExitVehicle() //Out of car, stop all services!
        }

    }

    companion object {
        private const val TAG = "ActivityProcessor"
    }
}