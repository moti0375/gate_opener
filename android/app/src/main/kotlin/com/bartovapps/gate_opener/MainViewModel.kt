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
    fun dispatchEvent(event: MainViewModelEvent) : Any {
        return when(event){
            is DeleteGate -> deleteGate(event.params)
            is CreateGate -> createGate(event.params)
        }
    }

    private  fun createGate(params: Map<String, Any>) : Any {
        Log.i("MainViewModel", "createGate: params: $params")
        return executeOperation { repository.addNewGate(params) }
    }

    private  fun deleteGate(params: Map<String, Any>) : Any {
        Log.i("MainViewModel", "deleteGate: params: $params")
        return executeOperation { repository.deleteGate(params) }
    }

    private  fun executeOperation( operation :() -> Any) {
        viewModelScope.launch(Dispatchers.IO) {
            operation()
        }
    }
}

sealed class MainViewModelEvent
class DeleteGate(val params: Map<String, Any>) : MainViewModelEvent()
class CreateGate(val params: Map<String, Any>) : MainViewModelEvent()