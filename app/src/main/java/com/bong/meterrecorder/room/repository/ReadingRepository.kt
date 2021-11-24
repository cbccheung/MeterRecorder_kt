package com.bong.meterrecorder.room.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.bong.meterrecorder.room.daos.ReadingDAO
import com.bong.meterrecorder.room.entities.Reading

class ReadingRepository (private val dao: ReadingDAO) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allItems: LiveData<List<Reading>> = dao.getAll()

    fun getItem(id: Long): LiveData<Reading>{
        return dao.getItem(id)
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(vararg item: Reading) {
        dao.insert(*item)
    }

    @WorkerThread
    suspend fun update(vararg item: Reading) {
        dao.update(*item)
    }

    @WorkerThread
    suspend fun delete(vararg item: Reading) {
        dao.delete(*item)
    }

}