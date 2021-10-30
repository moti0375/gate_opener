package com.bartovapps.gate_opener.core.dialer
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.telecom.TelecomManager
import android.telecom.PhoneAccount
import android.util.Log
import com.bartovapps.gate_opener.utils.PermissionsHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


private const val TAG = "Caller"
class DialerImpl @Inject constructor(@ApplicationContext private val context: Context) : Dialer {
    private val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    @SuppressLint("MissingPermission")
    override fun makeCall(phoneNumber: String) {
        Log.i(TAG, "Calling..")
        if(PermissionsHelper.hasPhoneCallsPermission(context)){
            Log.i(TAG, "Calling: $phoneNumber")
            val uri: Uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, phoneNumber, null)
            Log.i(TAG, "Calling: Uri: $uri")
            telecomManager.placeCall(uri, null)
        } else {
            Log.i(TAG, "No permissions")
        }
    }
}