package com.example.myschedule.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Schedule::class],
    exportSchema = false, version = 2)
abstract class MyDatabase : RoomDatabase() {
    abstract fun getMyDao() : MyDAO
    companion object {
        private var INSTANCE: MyDatabase? = null
        fun getDatabase(context: Context) : MyDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context, MyDatabase::class.java, "schedule_database")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE as MyDatabase
        }
    }
}
@Database(entities = [Schedule::class],
    exportSchema = false, version = 2)
abstract class MyDailyDatabase : RoomDatabase() {
    abstract fun getMyDao() : MyDAO
    companion object {
        private var INSTANCE: MyDailyDatabase? = null
        fun getDatabase(context: Context) : MyDailyDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context, MyDailyDatabase::class.java, "Daily_schedule_database")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE as MyDailyDatabase
        }
    }
}