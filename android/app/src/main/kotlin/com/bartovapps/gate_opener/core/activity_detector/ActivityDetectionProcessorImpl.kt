package com.bartovapps.gate_opener.core.activity_detector
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bartovapps.gate_opener.core.GateOpenerService
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.location.DetectedActivity.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityDetectionProcessorImpl @Inject constructor(@ApplicationContext private val context: Context) : ActivityDetectionProcessor {

    private var serviceStarted: Boolean = false

    override fun onActivitiesDetected(detectedActivities: List<DetectedActivity>) {
        val detectedActivity: DetectedActivity? = detectedActivities.firstOrNull { act -> act.confidence > 75 }
        Log.i(TAG, "processDetectedActivities: $detectedActivity")
//        serviceStarted = if(detectedActivity?.type == DetectedActivity.IN_VEHICLE && !serviceStarted){
//            //com.bartovapps.gate_opener.core.GateOpenerService.sendStartIntent(context)
//            true
//        } else {
//            val intent = Intent(context, com.bartovapps.gate_opener.core.GateOpenerService::class.java)
//            context.stopService(intent)
//            false
//        }
    }

    override fun onActivityTransition(transitionResult: ActivityTransitionResult?) {
        Log.i(TAG, "onActivityEntered: ${transitionResult?.transitionEvents}")
        transitionResult?.transitionEvents?.forEach {
            when(it.activityType){
                WALKING -> Log.i(TAG, "Walking: ${it.transitionType}")
                STILL -> Log.i(TAG, "Still: ${it.transitionType}")
                IN_VEHICLE -> handleVehicleTransitionChange(it.transitionType)
            }
        }
    }

    private fun handleVehicleTransitionChange(transitionType: Int) {
        serviceStarted = if(transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER && !serviceStarted){
            Log.i(TAG, "handleVehicleTransitionChange: Entered vehicle")
            startLocationService()
            true
        } else {
            Log.i(TAG, "handleVehicleTransitionChange: Exit vehicle")
            stopService()
            false
        }
    }

    private fun startLocationService() {
        GateOpenerService.sendStartIntent(context)
    }

    private fun stopService() {
        val intent = Intent(context, GateOpenerService::class.java)
        context.stopService(intent)
    }

    companion object{
        private const val TAG = "ActivityProcessor"
    }
}