package com.bartovapps.gate_opener.core.manager

import android.location.Location
import com.bartovapps.gate_opener.model.Gate
import com.google.android.gms.maps.model.LatLng

interface GateOpenerManager {
    fun start()
    fun stopTracking()
    fun onExitVehicle()
    fun onReachedDestination()
    fun onEnteredVehicle()
    fun onGettingCloseToNearGate()
    fun onExitNearestGateZone()
    fun getNearestGate(currentLocation: Location) : Pair<Gate, Float>?
}