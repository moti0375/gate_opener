package com.bartovapps.gate_opener.core.activity_detector

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import com.bartovapps.gate_opener.analytics.event.ActivityRecognitionEvent
import com.bartovapps.gate_opener.analytics.manager.Analytics
import com.bartovapps.gate_opener.core.activators.Activator
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private val INTERESTING_TRANSITION = intArrayOf(
    DetectedActivity.IN_VEHICLE,
    DetectedActivity.WALKING,
)

@Singleton
class ActivityDetectorImpl @Inject constructor(@ApplicationContext context: Context, val analytics: Analytics) : Activator {
    private val mActivityRecognitionClient = ActivityRecognition.getClient(context)
    private val intent: Intent = Intent(context, ActivityDetectionReceiver::class.java)
    private val mActivityTransitionPendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)


    override fun activate() {
        Log.i(TAG, "activate: ")
        val transitions = mutableListOf<ActivityTransition>()

        for (activity in INTERESTING_TRANSITION) {
            transitions.add(
                ActivityTransition.Builder()
                    .setActivityType(activity)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build()
            )
//            transitions.add(
//                ActivityTransition.Builder()
//                    .setActivityType(activity)
//                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
//                    .build()
//            )
        }

        val request = ActivityTransitionRequest(transitions)
        val transitionTask: Task<Void> = mActivityRecognitionClient.requestActivityTransitionUpdates(request, mActivityTransitionPendingIntent)
        val task: Task<Void> = mActivityRecognitionClient.requestActivityUpdates(ACTIVITY_UPDATES_INTERVAL, mActivityTransitionPendingIntent)
        task.addOnSuccessListener {
            analytics.sendEvent(ActivityRecognitionEvent(name = ActivityRecognitionEvent.EVENT_NAME.ACTIVITY_DETECT_STARTED))
            Log.i(TAG, "ActivityDetector sensor started successfully")
        }
        task.addOnFailureListener { e ->
            Log.e(TAG, "ActivityDetector sensor failed to start: ${e.message}")
            analytics.sendEvent(ActivityRecognitionEvent(name = ActivityRecognitionEvent.EVENT_NAME.ACTIVITY_DETECT_FAILURE).setDetails(bundle = bundleOf("details" to e.cause)))
        }
        transitionTask.addOnSuccessListener {
            Log.i(TAG, "Transition sensor started successfully")
            analytics.sendEvent(ActivityRecognitionEvent(name = ActivityRecognitionEvent.EVENT_NAME.TRANSITION_DETECT_STARTED))
        }
        transitionTask.addOnFailureListener { e ->
            Log.e(TAG, "Transition sensor failed to start: ${e.message}")
            analytics.sendEvent(ActivityRecognitionEvent(name = ActivityRecognitionEvent.EVENT_NAME.ACTIVITY_DETECT_FAILURE).setDetails(bundle = bundleOf("details" to e.cause)))
        }
    }

    override fun deactivate() {
        mActivityRecognitionClient.removeActivityUpdates(mActivityTransitionPendingIntent)
    }

    companion object {
        private const val TAG = "ActivityDetector"
        private const val ACTIVITY_UPDATES_INTERVAL = 5000L
    }
}