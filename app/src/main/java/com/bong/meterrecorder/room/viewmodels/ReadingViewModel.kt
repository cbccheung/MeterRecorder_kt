package com.bong.meterrecorder.room.viewmodels

import android.app.Application
import androidx.arch.core.util.Function
import androidx.lifecycle.*
import com.bong.meterrecorder.room.entities.Reading
import com.bong.meterrecorder.room.repository.ReadingRepository
import com.bong.meterrecorder.room.roomdb.DatabaseClient
import kotlinx.coroutines.launch
import androidx.lifecycle.Transformations


class ReadingViewModel(application: Application, initMeterId: Long) : AndroidViewModel(application){
    private val dao = DatabaseClient.getDatabase(application).readingDAO()
    private val repository = ReadingRepository(dao)
    val allItems = repository.getAllItems(initMeterId)


    /**
     * Launching a new coroutine to delete the data in a non-blocking way
     */
    fun delete(item: Reading) = viewModelScope.launch {
        repository.delete(item)
    }

    private val meterId = MutableLiveData<Long>()

    val items = Transformations.switchMap(meterId,
        { id: Long ->
            repository.getAllItems(id)
        }
    )


    fun getMeterId(): Long?{
        return meterId.value
    }

    fun setMeterId(id: Long){
        meterId.value = id
    }
}
