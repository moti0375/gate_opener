package com.bartovapps.gate_opener.data.datasource

import android.util.Log
import androidx.lifecycle.LiveData
import com.bartovapps.gate_opener.model.Gate
import com.bartovapps.gate_opener.storage.gates.GatesDao
import javax.inject.Inject

class GatesLocalDatasource @Inject constructor(private val gatesDao: GatesDao) : GatesDatasource{
    override fun getAll(): LiveData<List<Gate>> {
        return gatesDao.getAll()
    }

    override fun insert(gate: Gate) {
        Log.i("GatesLocalDataSource", "insert $gate")
        gatesDao.insertGate(gate)
    }

    override fun delete(gate: Gate) {
        gatesDao.delete(gate)
    }
}