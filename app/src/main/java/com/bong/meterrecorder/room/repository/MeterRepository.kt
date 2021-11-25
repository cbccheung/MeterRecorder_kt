package com.bong.meterrecorder.room.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.bong.meterrecorder.room.daos.MeterDAO
import com.bong.meterrecorder.room.entities.Meter

class MeterRepository (private val dao: MeterDAO) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allItems: LiveData<List<Meter>> = dao.getAll()

    fun getItem(id: Long): LiveData<Meter>{
        return dao.getItem(id)
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(vararg item: Meter) {
        dao.insert(*item)
    }

    @WorkerThread
    suspend fun update(vararg item: Meter) {
        dao.update(*item)
    }

    @WorkerThread
    suspend fun delete(vararg item: Meter) {
        dao.delete(*item)
    }

}