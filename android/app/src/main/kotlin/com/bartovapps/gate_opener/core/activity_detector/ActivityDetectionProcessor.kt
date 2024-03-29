package com.bartovapps.gate_opener.core.activity_detector
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

interface ActivityDetectionProcessor {
    fun onActivitiesDetected(detectedActivities: List<DetectedActivity>)
    fun onActivityTransition(transitionResult: ActivityTransitionResult?)
}