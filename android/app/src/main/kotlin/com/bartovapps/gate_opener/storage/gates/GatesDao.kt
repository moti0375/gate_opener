package com.bartovapps.gate_opener.storage.gates

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bartovapps.gate_opener.model.Gate

@Dao
interface GatesDao {
    @Query("SELECT * FROM gate")
    fun getAll() : LiveData<List<Gate>>

    @Query("SELECT * FROM gate WHERE id=:id")
    fun findById(id: String) : LiveData<Gate>

    @Insert
    fun insertGate(gate: Gate) : Long

    @Delete
    fun delete(gate: Gate)
}