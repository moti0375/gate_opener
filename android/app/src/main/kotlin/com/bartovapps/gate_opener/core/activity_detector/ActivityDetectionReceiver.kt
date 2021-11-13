package com.bartovapps.gate_opener.core.activity_detector
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ActivityDetectionReceiver : BroadcastReceiver() {

    @Inject lateinit var activityDetectionProcessor: ActivityDetectionProcessor
    override fun onReceive(context: Context, intent: Intent?) {


        Log.i(TAG, "onReceive:  ${intent?.extras}")
        intent?.let {
            if(ActivityTransitionResult.hasResult(it)){
                Log.i(TAG, "onReceive:  hasTransition..")
                activityDetectionProcessor.onActivityTransition(ActivityTransitionResult.extractResult(it))
            }

            if (ActivityRecognitionResult.hasResult(it)) {
                Log.i(TAG, "onReceive: hasResult: ${it.toString()} ")
                processDetectedActivities(ActivityRecognitionResult.extractResult(it)?.probableActivities)
            }
        }
    }

    private fun processDetectedActivities(activities : List<DetectedActivity>?){
        activities?.let {
            activityDetectionProcessor.onActivitiesDetected(it)
        }
    }

    companion object{
        private const val TAG = "ActivityReceiver"
    }
}
