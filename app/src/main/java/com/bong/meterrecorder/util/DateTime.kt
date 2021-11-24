package com.bong.meterrecorder.util

import java.text.SimpleDateFormat
import java.util.*

class DateTime {
    private val mCal: Calendar

    private constructor() {
        mCal = Calendar.getInstance()
    }

    constructor(millis: Long) {
        mCal = Calendar.getInstance()
        mCal.timeInMillis = millis
    }

    constructor(date: String, pattern: String) {
        mCal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        val millis = dateFormat.parse(date).time
        mCal.timeInMillis = millis
    }

    constructor(year: Int, month: Int, dayOfMonth: Int) {
        mCal = Calendar.getInstance()
        mCal.clear()
        mCal[Calendar.YEAR] = year
        val maxMonths = mCal.getActualMaximum(Calendar.MONTH)
        mCal[Calendar.MONTH] = Math.min(month, maxMonths)
        val maxDays = mCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        mCal[Calendar.DAY_OF_MONTH] = Math.min(dayOfMonth, maxDays)
    }

    constructor(calendar: Calendar) {
        mCal = calendar
    }

    fun toCalendar(): Calendar {
        return mCal
    }

    fun toTimeInMillis(): Long {
        return mCal.timeInMillis
    }

    fun toString(pattern: String?): String {
        val dateFormat = SimpleDateFormat(pattern)
        return dateFormat.format(mCal.time)
    }

    fun compareTo(dateTime: DateTime): Long {
        return mCal.timeInMillis - dateTime.mCal.timeInMillis
    }

    fun copy(): DateTime {
        return DateTime(mCal.timeInMillis)
    }

    override fun toString(): String {
        return toString(DATE_TIME_FORMAT)
    }

    companion object {
        var DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm"
        var DATE_FORMAT = "yyyy-MM-dd"
        var DATE_FORMAT_FULL = "yyyy-MM-dd (EEE)"
        var DATE_FORMAT_DISPLAY_FULL = "dd MMM yyyy, EEE"
        var DATE_FORMAT_DISPLAY_SIMPLE = "dd MMM, EEE"
        var TIME_FORMAT = "HH:mm"

        // For chars only
        var DATE_FORMAT_CHART_DAY = "dd/MM"
        var DATE_FORMAT_CHART_MONTH = "MM/yyyy"
        var DATE_FORMAT_CHART_YEAR = "yyyy"
        fun now(): DateTime {
            return DateTime()
        }
    }
}