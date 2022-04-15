package com.bong.meterrecorder.main

import android.app.Application
import androidx.lifecycle.*
import com.bong.meterrecorder.room.entities.Reading
import com.bong.meterrecorder.room.entities.extras.ReadingWithPrev
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

    val meter = Transformations.switchMap(meterId
    ){ id: Long->
        meterRepos.getItem(id)
    }

    val readings = Transformations.map(
        Transformations.switchMap(meterId
        ) { id: Long ->
            // Get all items from database
            readingRepos.getAllItems(id)
        }

    ) {
        // Data manipulation here
        val result = arrayListOf<ReadingWithPrev>()
        it.forEachIndexed { index: Int, reading: Reading ->
            val nextReading = if (index < it.size - 1) it[index + 1] else null

            result.add(
                ReadingWithPrev(
                    reading = reading,
                    prevReading = nextReading
                )
            )
        }

        result
    }


    fun setMeterId(id: Long){
        this.meterId.postValue(id)
    }
}
