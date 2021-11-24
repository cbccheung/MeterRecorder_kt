package com.bong.meterrecorder.room.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bong.meterrecorder.room.entities.Reading

@Dao
interface ReadingDAO {

    @Insert
    suspend fun insert(vararg items: Reading)

    @Update
    suspend fun update(vararg items: Reading)

    @Delete
    suspend fun delete(vararg items: Reading)

    @Query("SELECT * FROM Reading ORDER BY timeStamp DESC, id DESC")
    fun getAll() : LiveData<List<Reading>>

    @Query("SELECT * FROM Reading WHERE id = :id")
    fun getItem(id: Long) : LiveData<Reading>

}