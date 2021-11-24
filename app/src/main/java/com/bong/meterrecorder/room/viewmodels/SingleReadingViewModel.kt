package com.bong.meterrecorder.room.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bong.meterrecorder.room.entities.Reading
import com.bong.meterrecorder.room.repository.ReadingRepository
import com.bong.meterrecorder.room.roomdb.DatabaseClient
import kotlinx.coroutines.launch

class SingleReadingViewModel(application: Application, id: Long) : AndroidViewModel(application){
    private val dao = DatabaseClient.getDatabase(application).readingDAO()
    private val repository = ReadingRepository(dao)
    //val item = repository.getItem(id)

    val item = if(id == 0L){
        getNewItem()
    } else {
        repository.getItem(id)
    }

    /**
     * Launching a new coroutine to insert or update the data in a non-blocking way
     */
    fun insert(item: Reading) = viewModelScope.launch {
        repository.insert(item)
    }

    fun update(item: Reading) = viewModelScope.launch {
        repository.update(item)
    }

    fun getNewItem(): MutableLiveData<Reading> {
        val now = System.currentTimeMillis()
        val result = MutableLiveData<Reading>()
        result.postValue(
            Reading(
                value = Float.NaN,
                timeStamp = now,
                modified = now
            ))
        return result
    }
}
