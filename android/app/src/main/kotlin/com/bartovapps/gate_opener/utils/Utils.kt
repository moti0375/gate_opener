package com.bartovapps.gate_opener.utils
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bartovapps.gate_opener.R

const val FG_CHANNEL = "GATE_OPENENR"
const val REQUEST_CODE = 0
const val MSEC_FACTOR = 0.277778
internal fun createAppNotification(context: Context): Notification {

    createNotificationChannel(context)

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        Intent(""),
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builderCompat = NotificationCompat.Builder(context, FG_CHANNEL)
        .setContentTitle(context.getString(R.string.app_name))
        .setContentText(context.getString(R.string.gate_radar_notification_message))
        .addAction(REQUEST_CODE, "", pendingIntent)
        .setSmallIcon(R.drawable.ic_parking_barrier)
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_action_parking_barrier_black))

    return builderCompat.build()
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(FG_CHANNEL, FG_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = FG_CHANNEL
        channel.enableLights(false)
        channel.enableVibration(false)
        channel.vibrationPattern = longArrayOf(0)
        channel.lightColor = Color.RED
        channel.setSound(null, null)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val notificationManager = context.getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun verifyMinimumSdk(minimumSdk : Int) : Boolean {
    return Build.VERSION.SDK_INT >= minimumSdk
}

fun kmhToMsec(kmh: Int) =  kmh * MSEC_FACTOR