package com.bartovapps.gate_opener.analytics.manager

import com.bartovapps.gate_opener.analytics.endpoint.AnalyticsEndpoint
import com.bartovapps.gate_opener.analytics.event.Event

class AnalyticsManager constructor(private val endpoints: List<AnalyticsEndpoint>) : Analytics {
    override fun sendEvent(event: Event) {
        endpoints.forEach {
            it.sendEvent(event)
        }
    }
}