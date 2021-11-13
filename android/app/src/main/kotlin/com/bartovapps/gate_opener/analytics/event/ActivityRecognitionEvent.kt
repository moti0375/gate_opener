package com.bartovapps.gate_opener.analytics.event

import android.os.Bundle

class ActivityRecognitionEvent constructor(name: EVENT_NAME): Event(name = name.name) {
     fun setDetails(bundle: Bundle) : Event{
          parameters.putAll(bundle)
          return this
     }
     enum class EVENT_NAME{
          ENTERED_VEHICLE, EXIT_VEHICLE, TRANSITION_DETECT_STARTED, ACTIVITY_DETECT_STARTED, ACTIVITY_DETECT_FAILURE
     }
}