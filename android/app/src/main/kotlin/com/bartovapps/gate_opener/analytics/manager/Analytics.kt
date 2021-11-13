package com.bartovapps.gate_opener.analytics.manager

import com.bartovapps.gate_opener.analytics.event.Event

interface Analytics {
    fun sendEvent(event: Event)
}