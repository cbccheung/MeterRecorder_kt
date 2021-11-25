package com.bong.meterrecorder.room.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bong.meterrecorder.room.entities.Reading
import com.bong.meterrecorder.room.repository.ReadingRepository
import com.bong.meterrecorder.room.roomdb.DatabaseClient
import kotlinx.coroutines.launch

class ReadingViewModel(application: Application, meterId: Long) : AndroidViewModel(application){
    private val dao = DatabaseClient.getDatabase(application).readingDAO()
    private val repository = ReadingRepository(dao)
    val allItems = repository.getAllItems(meterId)


    /**
     * Launching a new coroutine to delete the data in a non-blocking way
     */
    fun delete(item: Reading) = viewModelScope.launch {
        repository.delete(item)
    }
}
