package com.bartovapps.gate_opener

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bartovapps.gate_opener.data.repository.GatesRepository
import com.bartovapps.gate_opener.model.Gate
import com.bartovapps.gate_opener.model.Location
import com.bartovapps.gate_opener.model.serializeToMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: GatesRepository): ViewModel() {

    val gatesMutableLiveData = repository.fetchAllGates()

    init {
        loadAvailableGates()
    }

    private fun loadAvailableGates() {

        val gates = mutableListOf<Gate>()
        gates.add(gate1)
        gates.add(gate2)

        executeOperation { repository.addNewGate(gate2.serializeToMap()) }
    }

    private fun executeOperation( operation :() -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            operation()
        }
    }
    companion object{
        val gate1 = Gate(id = "abc", name = "Nirim 4 Gate", location = Location(latitude = 34.6, longitude = 32.8), phoneNumber = "0545678765")
        val gate2 = Gate(id = "abcd", name = "Megido airfield", location = Location(latitude = 36.6, longitude = 38.8), phoneNumber = "0525780876")
    }
}