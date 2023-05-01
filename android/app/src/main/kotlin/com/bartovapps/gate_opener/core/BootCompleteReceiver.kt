package com.bartovapps.gate_opener.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootCompleteReceiver : BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.i("BootCompleteReceiver", "onReceive")
    }
}