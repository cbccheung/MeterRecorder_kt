package com.bong.meterrecorder.util

import android.content.Context
import androidx.preference.PreferenceManager

class SettingsHelper(val context: Context) {
    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    companion object{
        private const val KEY_CURRENT_METER_ID = "KEY_CURRENT_METER_ID"
    }

    var meterId: Long
        get() {
            return pref.getLong(KEY_CURRENT_METER_ID, 0)
        }
        set(value) {
            pref.edit().putLong(KEY_CURRENT_METER_ID, value).apply()
        }
}