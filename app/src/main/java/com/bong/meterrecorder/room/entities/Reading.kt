package com.bong.meterrecorder.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(
    entity = Meter::class,
    parentColumns = ["id"],
    childColumns = ["meterId"],
    onDelete = ForeignKey.CASCADE)
])
data class Reading(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var meterId: Long,
    var value: Float,
    var timeStamp: Long,
    var modified: Long
)
