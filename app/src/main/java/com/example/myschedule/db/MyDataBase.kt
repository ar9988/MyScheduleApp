package com.example.myschedule.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Schedule::class],
    exportSchema = false, version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun getMyDao() : MyDAO
    companion object {
        private var INSTANCE: MyDatabase? = null
        fun getDatabase(context: Context) : MyDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context, MyDatabase::class.java, "schedule_database")
                    .build()
            }
            return INSTANCE as MyDatabase
        }
    }
}