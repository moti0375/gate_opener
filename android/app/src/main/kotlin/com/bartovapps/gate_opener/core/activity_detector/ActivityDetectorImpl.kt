package com.bartovapps.gate_opener.core.activity_detector
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.util.Log
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
    DetectedActivity.STILL,
    DetectedActivity.ON_FOOT,
)

private const val TAG = "ActivityManager"
@Singleton
class ActivityDetectorImpl @Inject constructor(@ApplicationContext context: Context) : ActivityDetector {
    private val mActivityRecognitionClient = ActivityRecognition.getClient(context)
    private var intent: Intent = Intent(context, ActivityDetectionReceiver::class.java)
    private val mActivityTransitionPendingIntent = PendingIntent.getBroadcast(context, 1, intent, FLAG_UPDATE_CURRENT)


    override fun start() {
        Log.i(TAG, "start: ")
        val transitions = mutableListOf<ActivityTransition>()

        for (activity in INTERESTING_TRANSITION) {
            transitions.add(
                ActivityTransition.Builder()
                    .setActivityType(activity)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build()
            )
            transitions.add(
                ActivityTransition.Builder()
                    .setActivityType(activity)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                    .build()
            )
        }

        val request = ActivityTransitionRequest(transitions)
        val transitionTask: Task<Void> = mActivityRecognitionClient.requestActivityTransitionUpdates(request, mActivityTransitionPendingIntent)
        val task: Task<Void> = mActivityRecognitionClient.requestActivityUpdates(1000, mActivityTransitionPendingIntent)
        task.addOnSuccessListener { Log.i("ActivityManager", "com.bartovapps.gate_opener.core.activity_detector.ActivityDetector sensor started successfully") }
        task.addOnFailureListener { e -> Log.e("ActivityManager", "com.bartovapps.gate_opener.core.activity_detector.ActivityDetector sensor failed to start: ${e.message}") }
        transitionTask.addOnSuccessListener {
            Log.i("ActivityManager", "Transition sensor started successfully")
        }
        transitionTask.addOnFailureListener {
                e -> Log.e("ActivityManager", "Transition sensor failed to start: ${e.message}")
        }
    }

    override fun stop() {
        mActivityRecognitionClient.removeActivityUpdates(mActivityTransitionPendingIntent)
    }

    companion object{
        private const val DETECTION_INTERVAL = 5000L
    }
}