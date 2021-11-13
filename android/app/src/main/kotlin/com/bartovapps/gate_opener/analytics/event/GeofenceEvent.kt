package com.bartovapps.gate_opener.analytics.event

import android.os.Bundle

class GeofenceEvent constructor(eventName: EVENT_NAME): Event(name = eventName.name){
    fun setDetails(details: Bundle) : Event {
        parameters.putAll(details)
        return this
    }
    enum class EVENT_NAME{
        ENTERED_GEOFENCE, EXIT_GEOFENCE, ARRIVED_DESTINATION
    }
}