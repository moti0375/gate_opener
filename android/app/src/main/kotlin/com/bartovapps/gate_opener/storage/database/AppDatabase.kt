package com.bartovapps.gate_opener.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bartovapps.gate_opener.model.Gate
import com.bartovapps.gate_opener.storage.gates.GatesDao

@Database(entities = [Gate::class], version = 1)
@TypeConverters(LocationConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gatesDao() : GatesDao
}