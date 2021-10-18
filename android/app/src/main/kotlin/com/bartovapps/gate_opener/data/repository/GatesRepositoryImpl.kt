package com.bartovapps.gate_opener.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.bartovapps.gate_opener.data.datasource.GatesDatasource
import com.bartovapps.gate_opener.model.serializeToMap
import com.bartovapps.gate_opener.model.toDataClass
import javax.inject.Inject

class GatesRepositoryImpl @Inject constructor(private val localDatasource: GatesDatasource) : GatesRepository{
    override fun fetchAllGates(): LiveData<List<Map<String, Any>>> {
        return Transformations.map(localDatasource.getAll()) { gate -> gate.map { e -> e.serializeToMap() } }
    }

    override fun addNewGate(gate: Map<String, Any>) {
        localDatasource.insert(gate.toDataClass())
    }

    override fun deleteGate(gate: Map<String, Any>) {
        localDatasource.delete(gate.toDataClass())
    }

}