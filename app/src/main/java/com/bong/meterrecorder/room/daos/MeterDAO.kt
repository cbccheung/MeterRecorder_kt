package com.bong.meterrecorder.room.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bong.meterrecorder.room.entities.Meter

@Dao
interface MeterDAO {

    @Insert
    suspend fun insert(vararg items: Meter)

    @Update
    suspend fun update(vararg items: Meter)

    @Delete
    suspend fun delete(vararg items: Meter)

    @Query("SELECT * FROM Meter ORDER BY name")
    fun getAll() : LiveData<List<Meter>>

    @Query("SELECT * FROM Meter WHERE id = :id")
    fun getItem(id: Long) : LiveData<Meter>

}