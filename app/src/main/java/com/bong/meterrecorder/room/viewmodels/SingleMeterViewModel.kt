package com.bong.meterrecorder.room.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bong.meterrecorder.room.entities.Meter
import com.bong.meterrecorder.room.repository.MeterRepository
import com.bong.meterrecorder.room.roomdb.DatabaseClient
import kotlinx.coroutines.launch

class SingleMeterViewModel(application: Application, id: Long) : AndroidViewModel(application){
    private val dao = DatabaseClient.getDatabase(application).meterDAO()
    private val repository = MeterRepository(dao)

    val item = if(id == 0L){
        getNewItem()
    } else {
        repository.getItem(id)
    }

    /**
     * Launching a new coroutine to insert or update the data in a non-blocking way
     */
    fun insert(item: Meter) = viewModelScope.launch {
        repository.insert(item)
    }

    fun update(item: Meter) = viewModelScope.launch {
        repository.update(item)
    }

    fun getNewItem(): MutableLiveData<Meter> {
        val result = MutableLiveData<Meter>()
        result.postValue(
            Meter(
                name = ""
            ))
        return result
    }
}
