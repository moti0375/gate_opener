package com.bartovapps.gate_opener.core.activators

interface Activator {
    fun isValid() : Boolean
    fun activate()
    fun deactivate()
    fun getName() : String
}