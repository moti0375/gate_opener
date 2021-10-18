package com.bartovapps.gate_opener

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartovapps.gate_opener.model.Gate
import com.bartovapps.gate_opener.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {

    private val gatesMutableLiveData = MutableLiveData<List<Gate>>()
    val gatesLiveData: LiveData<List<Gate>>
    get() = gatesMutableLiveData

    init {
        loadAvailableGates()
    }

    private fun loadAvailableGates() {
        val gates = mutableListOf<Gate>()
        gates.add(gate1)
        gates.add(gate2)
        gatesMutableLiveData.postValue(gates)
    }

    companion object{
        val gate1 = Gate(id = "abc", name = "Nirim 4 Gate", location = Location(latitude = 34.6, longitude = 32.8), phoneNumber = "0545678765")
        val gate2 = Gate(id = "abcd", name = "Megido airfield", location = Location(latitude = 36.6, longitude = 38.8), phoneNumber = "0525780876")
    }
}