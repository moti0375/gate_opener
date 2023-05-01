package com.bartovapps.gate_opener.app_initializer

import android.content.Context
import android.util.Log
import androidx.startup.Initializer

class AppInitializer : Initializer<Unit> {
    override fun create(context: Context) : Unit {
        Log.i("AppInitializer", "create: ")
        return
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}