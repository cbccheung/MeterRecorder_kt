package com.bong.meterrecorder.room.entities.extras

import com.bong.meterrecorder.room.entities.Reading

class ReadingWithPrev(
    val reading: Reading,
    val prevReading: Reading?
) {

    override fun equals(other: Any?): Boolean {
        return other is ReadingWithPrev && reading == other.reading && prevReading == other.prevReading
    }

    override fun hashCode(): Int {
        var result = reading.hashCode()
        result = 31 * result + (prevReading?.hashCode() ?: 0)
        return result
    }


}


