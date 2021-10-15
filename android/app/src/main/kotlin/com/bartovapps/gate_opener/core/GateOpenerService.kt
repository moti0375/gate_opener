package com.bartovapps.gate_opener.core
import com.bartovapps.gate_opener.core.dialer.DialerImpl
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.bartovapps.gateopener.utils.createAppNotification

class GateOpenerService : Service() {

    private lateinit var locationHelper : LocationHelper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(!this::locationHelper.isInitialized){
            val caller = DialerImpl(this.applicationContext)
            locationHelper = LocationHelper(context = this.applicationContext, caller)
        }
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
                START_STICKY
            }
            else -> {
                stopSelf() // Stop all the instances
                START_NOT_STICKY
            }
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