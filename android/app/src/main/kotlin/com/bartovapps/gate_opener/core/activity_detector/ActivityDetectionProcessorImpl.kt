package com.bartovapps.gate_opener.core.activity_detector

import android.util.Log
import com.bartovapps.gate_opener.analytics.event.ActivityRecognitionEvent
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

    private var isInVehicle: Boolean = false
    private var stillPeriodTime: Long? = null

    override fun onActivitiesDetected(detectedActivities: List<DetectedActivity>) {
        Log.i(TAG, "onActivitiesDetected: detectedActivities: $detectedActivities")
        val highestConfidenceActivity: DetectedActivity? = detectedActivities.filter { it.type != TILTING && it.type != UNKNOWN }.maxByOrNull { act -> act.confidence }
        Log.i(TAG, "processDetectedActivities: highestConfidenceActivity: $highestConfidenceActivity, inVehicle: $isInVehicle")
        isInVehicle = highestConfidenceActivity?.let {
            if (it.type == IN_VEHICLE && it.confidence >= VEHICLE_HIGH_CONFIDENCE) {
                stillPeriodTime = null
                if (!isInVehicle) {  //User starts driving car!!
                    handleVehicleTransitionChange(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    true
                } else {
                    isInVehicle
                }
            } else {
                if (isInVehicle) {
                    when {
                        it.type == STILL -> { //The user was in vehicle but the highest is STILL, maybe it is in red light (and engine is off..)
                            stillPeriodTime?.let { stillTime ->
                                Log.i(TAG, "Stopped: stillTime: $stillTime")
                                val stopPeriod = System.currentTimeMillis() - stillTime
                                Log.i(TAG, "Stopped: stopPeriod: $stopPeriod")
                                if (stopPeriod >= STILL_THRESHOLD) { //It is standing still too much.. Probably out of car or engine turned off for long time, out of the car!
                                    Log.i(TAG, "Long still, exit vehicle")
                                    handleVehicleTransitionChange(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                                    false
                                } else { //Lets wait a bit..
                                    Log.i(TAG, "Stopped, but not enough for exit vehicle")
                                    isInVehicle
                                }
                            } ?: run { //User stopped, lets start measure its still period
                                Log.i(TAG, "Stopped during driving.. ")
                                stillPeriodTime = System.currentTimeMillis()
                                isInVehicle
                            }
                        }
                        it.confidence >= ACTIVITY_CONFIDENCE -> { //It none vehicle activity (WALKING, ON_FOOT, RUNNING etc') in good confidence Out of car!!!
                            handleVehicleTransitionChange(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                            stillPeriodTime = null
                            false
                        }
                        else -> { //Some other activity rather then still, low confidence so reset still timer and return inVehicle as is
                            stillPeriodTime = null
                            isInVehicle
                        }
                    }
                } else { //The user wasn't in vehicle, keep state as is
                    isInVehicle
                }

            }
        } ?: isInVehicle //Couldn't find a highest activity.. Keep vehicle state as is
    }

    override fun onActivityTransition(transitionResult: ActivityTransitionResult?) {
//        Log.i(TAG, "onActivityTransition: ${transitionResult?.transitionEvents}")
//        transitionResult?.transitionEvents?.firstOrNull { it.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER }?.let {
//            if(it.activityType == IN_VEHICLE){
//                handleVehicleTransitionChange(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
//            } else {
//                handleVehicleTransitionChange(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
//
//            }
//        }
    }

    private fun handleVehicleTransitionChange(transitionType: Int) {
        if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
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
        private const val STILL_THRESHOLD = 60000
        private const val VEHICLE_HIGH_CONFIDENCE = 75
        private const val ACTIVITY_CONFIDENCE = 45
    }
}