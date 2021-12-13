package com.bartovapps.gate_opener.core

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.bartovapps.gate_opener.analytics.event.GeofenceEvent
import com.bartovapps.gate_opener.analytics.manager.Analytics
import com.bartovapps.gate_opener.core.location.LocationHelper
import com.bartovapps.gate_opener.core.manager.GateOpenerManagerImpl.Companion.GATE_OPENER_NOTIFICATION_ID
import com.bartovapps.gate_opener.utils.createAppNotification
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GateOpenerService : Service() {

    @Inject
    lateinit var locationHelper: LocationHelper

    @Inject
    lateinit var analytics: Analytics

    override fun onCreate() {
        super.onCreate()
        analytics.sendEvent(GeofenceEvent(eventName = GeofenceEvent.EVENT_NAME.GATE_OPENER_SERVICE_STARTED))
        val notification = createAppNotification(context = this.applicationContext)
        startForeground(GATE_OPENER_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            terminate()
            return START_NOT_STICKY
        }
        val action = intent.action
        if (action == null) {
            terminate()
            return START_NOT_STICKY
        }

        return handleStartCommand(action)
    }

    private fun handleStartCommand(action: String): Int {
        Log.i(TAG, "handleStartCommand: action: $action")
        return when (action) {
            ACTION_START -> {
                locationHelper.startListenToLocationUpdates()
                START_STICKY
            }
            else -> {
                terminate()
                START_NOT_STICKY
            }
        }
    }

    private fun terminate() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy:")
        analytics.sendEvent(GeofenceEvent(eventName = GeofenceEvent.EVENT_NAME.GATE_OPENER_SERVICE_STOPPED))
        locationHelper.stopListenToLocationUpdates()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val TAG = "XXX: GateOpenerService"
        private const val ACTION_START = "GateOpenerService.start"

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