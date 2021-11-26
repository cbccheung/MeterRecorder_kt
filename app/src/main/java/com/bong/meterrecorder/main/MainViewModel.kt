package com.bong.meterrecorder.main

import android.app.Application
import androidx.lifecycle.*
import com.bong.meterrecorder.room.entities.Meter
import com.bong.meterrecorder.room.entities.Reading
import com.bong.meterrecorder.room.repository.MeterRepository
import com.bong.meterrecorder.room.repository.ReadingRepository
import com.bong.meterrecorder.room.roomdb.DatabaseClient
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application){

    private val meterDao = DatabaseClient.getDatabase(application).meterDAO()
    private val meterRepos = MeterRepository(meterDao)
    val meters = meterRepos.allItems

    private val readingDao = DatabaseClient.getDatabase(application).readingDAO()
    private val readingRepos = ReadingRepository(readingDao)


    /**
     * Launching a new coroutine to delete the data in a non-blocking way
     */
    fun delete(vararg item: Reading) = viewModelScope.launch {
        readingRepos.delete(*item)
    }



    // Readings
    val meterId = MutableLiveData<Long>()

    val readings = Transformations.switchMap(meterId,
        { id: Long ->
            readingRepos.getAllItems(id)
        }
    )


    fun setMeterId(id: Long){
        this.meterId.value = id
    }
}
