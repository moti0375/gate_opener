package com.bartovapps.gate_opener.analytics.endpoint

import com.bartovapps.gate_opener.analytics.event.Event

interface AnalyticsEndpoint {
    fun sendEvent(event: Event)
}