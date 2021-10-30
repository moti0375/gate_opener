package com.bartovapps.gate_opener.core
import android.app.AlarmManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.bartovapps.gate_opener.core.geofence.GateAlarmReceiver
import com.bartovapps.gate_opener.core.geofence.GatesGeofenceReceiver
import com.bartovapps.gate_opener.core.location.LocationHelper
import com.bartovapps.gate_opener.utils.createAppNotification
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GateOpenerService : Service() {

    @Inject
    lateinit var locationHelper : LocationHelper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf(startId)
            return START_NOT_STICKY
        }
        val action = intent.action
        if (action == null) {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        return handleStartCommand(action)
    }

    private fun handleStartCommand(action: String): Int {
        Log.i("com.bartovapps.gate_opener.core.GateOpenerService", "handleStartCommand: action: $action")
        return when (action) {
            ACTION_START -> {
                val notification = createAppNotification(context = this.applicationContext)
                startForeground(FOREGROUND_SERVICE_ID, notification)
                locationHelper.startListenToLocationUpdates()
                stopAlarmManager(this.applicationContext)
                START_STICKY
            }
            else -> {
                stopSelf() // Stop all the instances
                START_NOT_STICKY
            }
        }
    }

    private fun stopAlarmManager(applicationContext: Context?) {
        applicationContext?.let {
            val alarmManager = it.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(GateAlarmReceiver.getPendingIntent(it))
        }
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object{
        const val FOREGROUND_SERVICE_ID = 0xF91C0FE
        private const val ACTION_START = "com.bartovapps.gate_opener.core.GateOpenerService.start"
        /**
         * Send start command to the foreground service
         * @param context
         */
        fun sendStartIntent(context: Context) {
            val intent = Intent(context, GateOpenerService::class.java)
            intent.action = ACTION_START
            safeStartService(context, intent)
        }

        /**
         * Send intent to the service. Supports all android versions
         * @param context
         * @param intent
         */
        private fun safeStartService(context: Context, intent: Intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}