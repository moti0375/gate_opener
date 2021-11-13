package com.bartovapps.gate_opener.model

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()


data class Location(val latitude: Double, val longitude: Double)

@Entity
data class Gate(@PrimaryKey(autoGenerate = true) val id: Int = 0, val name: String, val location: Location, @ColumnInfo(name = "phone_number") val phoneNumber: String){
    fun toBundle() :Bundle{
        return bundleOf("name" to name, "location" to location.toString())
    }
}
fun <T> T.serializeToMap(): Map<String, Any> {
    return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
}

inline fun <reified T> Map<String, Any>.toDataClass(): T {
    return convert()
}
