package com.bartovapps.gate_opener.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()
data class Location(val latitude: Double, val longitude: Double)
data class Gate(val id: String, val name: String, val location: Location, val phoneNumber: String)

fun <T> T.serializeToMap(): Map<String, Any> {
    return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
}
