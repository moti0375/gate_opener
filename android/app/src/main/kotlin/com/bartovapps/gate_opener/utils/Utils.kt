package com.bartovapps.gate_opener.utils
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_MIN
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
        .setContentTitle("Gate Opener")
        .setContentText("Gate Opener is Running in the background")
        .addAction(REQUEST_CODE, "", pendingIntent)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))

    return builderCompat.build()
}


private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(FG_CHANNEL, FG_CHANNEL, IMPORTANCE_MIN)
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

fun kmhToMsec(kmh: Long) =  kmh * MSEC_FACTOR