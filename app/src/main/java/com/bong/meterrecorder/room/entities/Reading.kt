package com.bong.meterrecorder.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reading(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var value: Float,
    var timeStamp: Long,
    var modified: Long
)
