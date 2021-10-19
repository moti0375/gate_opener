package com.bartovapps.gate_opener

import android.util.Log
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
    }

    private fun executeOperation( operation :() -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            operation()
        }
    }

    fun dispatchEvent(event: MainViewModelEvent){
        when(event){
            is DeleteGate -> deleteGate(event.params)
            is CreateGate -> createGate(event.params)
        }
    }

    private fun createGate(params: Map<String, Any>) {
        Log.i("MainViewModel", "createGate: params: $params")
        executeOperation { repository.addNewGate(params) }
    }

    private fun deleteGate(params: Map<String, Any>) {
        Log.i("MainViewModel", "deleteGate: params: $params")
        executeOperation { repository.deleteGate(params) }
    }

    companion object{
        val gate1 = Gate(name = "Nirim 4 Gate", location = Location(latitude = 34.6, longitude = 32.8), phoneNumber = "0545678765")
        val gate2 = Gate(name = "Megido airfield", location = Location(latitude = 36.6, longitude = 38.8), phoneNumber = "0525780876")
    }
}

sealed class MainViewModelEvent
class DeleteGate(val params: Map<String, Any>) : MainViewModelEvent()
class CreateGate(val params: Map<String, Any>) : MainViewModelEvent()