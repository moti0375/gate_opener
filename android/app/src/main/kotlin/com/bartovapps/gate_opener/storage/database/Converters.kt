package com.bartovapps.gate_opener.storage.database

import androidx.room.TypeConverter
import com.bartovapps.gate_opener.model.Location
import com.google.gson.Gson

class LocationConverter {

    @TypeConverter
    fun toString(location: Location): String {
        return Gson().toJson(location)
    }

    @TypeConverter
    fun fromString(json: String): Location {
        return Gson().fromJson(json, Location::class.java)
    }
}