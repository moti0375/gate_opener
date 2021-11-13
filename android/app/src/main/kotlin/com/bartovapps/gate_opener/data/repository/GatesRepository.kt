package com.bartovapps.gate_opener.data.repository

import androidx.lifecycle.LiveData

interface GatesRepository {
    fun fetchAllGates() : LiveData<List<Map<String, Any>>>
    fun addNewGate(gate: Map<String, Any>) : Map<String, Long>
    fun deleteGate(gate: Map<String, Any>)
}