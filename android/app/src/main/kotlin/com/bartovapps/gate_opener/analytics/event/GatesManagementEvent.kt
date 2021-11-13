package com.bartovapps.gate_opener.analytics.event

import android.os.Bundle

class GatesManagementEvent constructor(eventName: EVENT_NAME): Event(name = eventName.name){
    enum class EVENT_NAME{
        GATE_ADDED, GATE_DELETED
    }
}