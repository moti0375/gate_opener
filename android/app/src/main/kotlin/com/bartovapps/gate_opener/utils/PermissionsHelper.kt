package com.bartovapps.gate_opener.utils
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

private val FOREGROUND =
    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

@RequiresApi(Build.VERSION_CODES.Q)
private val BACKGROUND = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

private val PHONE_CALLS = arrayOf(Manifest.permission.CALL_PHONE)

@RequiresApi(Build.VERSION_CODES.O)
private val MANAGE_CALLS = arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.MANAGE_OWN_CALLS)
object PermissionsHelper {

    private fun isSdkVersionFromQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    private fun isSdkVersionFromO() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O


    @RequiresApi(Build.VERSION_CODES.M)
    fun isLocationGranted(context: Context): Boolean {
        return when {
            isSdkVersionFromQ() -> isPermissionGranted(context, FOREGROUND) && isPermissionGranted(context, BACKGROUND)
            else -> isPermissionGranted(context, FOREGROUND)
        }
    }


    fun hasPhoneCallsPermission(context: Context) : Boolean{
        return when {
            isSdkVersionFromO() -> isPermissionGranted(context, MANAGE_CALLS)
            else -> isPermissionGranted(context, PHONE_CALLS)
        }
    }

    private fun isPermissionGranted(context: Context, manifestPermissions: Array<String>) : Boolean {
        val notGranted: String? = manifestPermissions.firstOrNull {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        return notGranted == null
    }



}