package com.bartovapps.gate_opener.core.geofence

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bartovapps.gate_opener.model.Gate
import com.bartovapps.gate_opener.storage.gates.GatesDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class GateOpenerWorker @AssistedInject constructor(@Assisted private val context: Context, @Assisted params: WorkerParameters, private val gatesDao: GatesDao) :
    CoroutineWorker(context, params) {

    var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override suspend fun doWork(): Result {
        Log.i(TAG, "doWork: ")
        val gates = gatesDao.getAllGates()
        if (gates.isEmpty()) {
            return Result.failure()
        }

        val closestGate = checkClosestGateDistance(gates) ?: return Result.retry()
        Log.i(TAG, "doWork: closest gate distance: ${closestGate.second}")
        if (closestGate.second < 500) {
            return Result.success()
        }

        return Result.retry()
    }

    @SuppressLint("MissingPermission")
    private fun checkClosestGateDistance(gates: List<Gate>): Pair<Gate, Float>? {
        val location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        Log.i(TAG, "lastKnownLocation: ${location?.latitude}:${location?.longitude}")
        val closestGate: Pair<Gate, Float>? = location?.let { lastLocation ->
            gates.map {
                Pair(it, Location("gate").apply {
                    latitude = it.location.latitude
                    longitude = it.location.longitude
                }.distanceTo(lastLocation))
            }.minByOrNull {
                it.second
            }
        }
        Log.i(TAG, "Closest gate: $closestGate")
        return closestGate
    }

    companion object {
        private const val TAG = "GateOpenerWorker"
    }
}