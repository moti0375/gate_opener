package com.bartovapps.gate_opener.analytics.endpoint

import android.os.Bundle
import android.util.Log
import com.bartovapps.gate_opener.analytics.event.Event
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseEndpoint @Inject constructor(private val firebase: FirebaseAnalytics) : AnalyticsEndpoint{
    override fun sendEvent(event: Event) {
        firebase.logEvent(event.name, event.parameters)
    }
}