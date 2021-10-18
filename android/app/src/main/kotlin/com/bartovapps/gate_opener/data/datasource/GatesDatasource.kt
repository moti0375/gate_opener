package com.bartovapps.gate_opener.data.datasource

import androidx.lifecycle.LiveData
import com.bartovapps.gate_opener.model.Gate

interface GatesDatasource {
    fun getAll() : LiveData<List<Gate>>
    fun insert(gate: Gate)
    fun delete(gate: Gate)
}