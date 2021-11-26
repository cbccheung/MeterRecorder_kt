package com.bong.meterrecorder.room.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bong.meterrecorder.room.daos.MeterDAO
import com.bong.meterrecorder.room.daos.ReadingDAO
import com.bong.meterrecorder.room.entities.Meter
import com.bong.meterrecorder.room.entities.Reading

@Database(entities = [Reading::class, Meter::class], version = 1)
abstract class DatabaseClient : RoomDatabase() {
    abstract fun readingDAO(): ReadingDAO
    abstract fun meterDAO(): MeterDAO

    companion object {
        @Volatile
        private var INSTANCE: DatabaseClient? = null

        fun getDatabase(context: Context): DatabaseClient {

            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseClient::class.java,
                    "master"
                ).addCallback(object: Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Insert the default item
                        db.execSQL("INSERT INTO Meter (name) values ('Meter')")
                    }
                })
                    .build()


                INSTANCE = instance
                // return instance
                instance
            }


        }
    }
}