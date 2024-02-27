package com.example.myschedule.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Schedule::class],
    exportSchema = false, version = 1)
abstract class MyPeriodScheduleDatabase : RoomDatabase() {
    abstract fun getMyDao() : MyDAO
    companion object {
        private var INSTANCE: MyPeriodScheduleDatabase? = null
        fun getDatabase(context: Context) : MyPeriodScheduleDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context, MyPeriodScheduleDatabase::class.java, "Period_schedule_database")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE as MyPeriodScheduleDatabase
        }
    }
}