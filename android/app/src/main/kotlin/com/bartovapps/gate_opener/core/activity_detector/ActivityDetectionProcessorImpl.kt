package com.bartovapps.gate_opener.core.activity_detector

import android.util.Log
import com.bartovapps.gate_opener.analytics.event.ActivityRecognitionEvent
import com.bartovapps.gate_opener.analytics.event.Event
import com.bartovapps.gate_opener.analytics.manager.Analytics
import com.google.android.gms.location.DetectedActivity.*
import javax.inject.Inject
import javax.inject.Singleton
import com.google.android.gms.location.*
import com.bartovapps.gate_opener.core.manager.GateOpenerManager


@Singleton
class ActivityDetectionProcessorImpl @Inject constructor(
    private val gateOpenerManager: GateOpenerManager,
    private val analytics: Analytics
) : ActivityDetectionProcessor {

    private var started = false

    override fun onActivitiesDetected(detectedActivities: List<DetectedActivity>) {
        val detectedActivity: DetectedActivity? = detectedActivities.firstOrNull { act -> act.confidence > 75 }
        Log.i(TAG, "processDetectedActivities: $detectedActivity, started: $started")
//        started = if(detectedActivity?.type == STILL){
//            if(!started){
//                handleVehicleTransitionChange(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
//            }
//            true
//        } else {
//            handleVehicleTransitionChange(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
//            false
//        }
    }

    override fun onActivityTransition(transitionResult: ActivityTransitionResult?) {
        Log.i(TAG, "onActivityTransition: ${transitionResult?.transitionEvents}")
        transitionResult?.transitionEvents?.forEach {
            when (it.activityType) {
                IN_VEHICLE -> handleVehicleTransitionChange(it.transitionType)
                else -> {
                    if(it.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) { //Starting activity other than vehicle
                        handleVehicleTransitionChange(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                    }
                }
            }
        }
    }

    private fun handleVehicleTransitionChange(transitionType: Int) {
        if(transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER){
            Log.i(TAG, "handleVehicleTransitionChange: Entered vehicle: ")
            analytics.sendEvent(ActivityRecognitionEvent(name = ActivityRecognitionEvent.EVENT_NAME.ENTERED_VEHICLE))
            gateOpenerManager.onEnteredVehicle() //Starting everything..
        } else {
            Log.i(TAG, "handleVehicleTransitionChange: Exit vehicle: ")
            analytics.sendEvent(ActivityRecognitionEvent(name = ActivityRecognitionEvent.EVENT_NAME.EXIT_VEHICLE))
            gateOpenerManager.onExitVehicle() //Out of car, stop all services!
        }

    }

    companion object {
        private const val TAG = "ActivityProcessor"
    }
}