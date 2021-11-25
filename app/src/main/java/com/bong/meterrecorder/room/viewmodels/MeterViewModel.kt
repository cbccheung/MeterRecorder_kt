package com.bong.meterrecorder.room.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bong.meterrecorder.room.entities.Meter
import com.bong.meterrecorder.room.repository.MeterRepository
import com.bong.meterrecorder.room.roomdb.DatabaseClient
import kotlinx.coroutines.launch

class MeterViewModel(application: Application) : AndroidViewModel(application){
    private val dao = DatabaseClient.getDatabase(application).meterDAO()
    private val repository = MeterRepository(dao)
    val allItems = repository.allItems


    /**
     * Launching a new coroutine to delete the data in a non-blocking way
     */
    fun delete(vararg item: Meter) = viewModelScope.launch {
        repository.delete(*item)
    }
}
