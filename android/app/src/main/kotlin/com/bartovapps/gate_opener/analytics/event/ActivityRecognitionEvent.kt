package com.bartovapps.gate_opener.analytics.event

class ActivityRecognitionEvent constructor(name: EVENT_NAME): Event(name = name.name) {
     enum class EVENT_NAME{
          ENTERED_VEHICLE, EXIT_VEHICLE
     }
}