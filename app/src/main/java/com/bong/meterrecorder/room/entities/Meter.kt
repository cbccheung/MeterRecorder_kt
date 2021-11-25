package com.bong.meterrecorder.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Meter(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String
)
